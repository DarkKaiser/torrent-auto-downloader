using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Data.SQLite;
using System.IO;
using System.Diagnostics;
using System.Xml;
using System.Net;
using System.Threading;

namespace JapanVocabularyDbManager
{
    public partial class frmMain : Form
    {
        private static string DB_FILE_NAME = "vocabulary_v3.db";

        private SQLiteConnection mDbConnection = null;

        public frmMain()
        {
            InitializeComponent();
        }

        #region 이벤트 핸들러

        private void frmMain_Load(object sender, EventArgs e)
        {
            // 프로그램을 초기화합니다.
            cboWordSearchItem.SelectedIndex = 2/* 설명 */;
            cboHanjaSearchItem.SelectedIndex = 3/* 뜻 */;

            // DB에 연결합니다.
            string errorMessage;
            if (ConnectDB(out errorMessage) == false)
            {
                MessageBox.Show("DB 연결이 실패하였습니다. 프로그램을 종료합니다.\r\n\r\n" + errorMessage, "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                Close();
                return;
            }

            // 전체 데이터를 화면에 뿌린다.
            FillData();
        }
        
        private void txtHanjaSearchWord_TextChanged(object sender, EventArgs e)
        {
            if (txtHanjaSearchWord.Text.Trim().Length == 0)
                btnHanjaSearch.Enabled = false;
            else
                btnHanjaSearch.Enabled = true;
        }

        private void btnHanjaAll_Click(object sender, EventArgs e)
        {
            FillHanjaData(string.Empty);
        }

        private void dataHanjaGridView_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyValue == 46/* Delete */)
            {
                DataGridViewSelectedRowCollection rc = dataHanjaGridView.SelectedRows;
                if (rc.Count != 1)
                {
                    MessageBox.Show("데이터를 삭제하시려면 하나의 행만 선택해주세요!");
                    e.Handled = true;
                    return;
                }
            }
        }

        private void dataHanjaGridView_UserDeletingRow(object sender, DataGridViewRowCancelEventArgs e)
        {
            DataGridViewSelectedRowCollection rc = dataHanjaGridView.SelectedRows;
            if (rc.Count != 1)
            {
                e.Cancel = true;
                return;
            }
                
            if (MessageBox.Show("선택하신 데이터를 완전히 삭제하시겠습니까?", "삭제", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.No)
                e.Cancel = true;
        }

        private void dataHanjaGridView_UserDeletedRow(object sender, DataGridViewRowEventArgs e)
        {
            using (SQLiteCommand cmd = mDbConnection.CreateCommand())
            {
                cmd.CommandText = string.Format("DELETE FROM TBL_HANJA WHERE idx = {0};", e.Row.Cells[0].Value);
                cmd.ExecuteNonQuery();
            }
        }

        private void btnHanjaSearch_Click(object sender, EventArgs e)
        {
            string searchWord = txtHanjaSearchWord.Text.Trim();
            Debug.Assert(string.IsNullOrEmpty(searchWord) == false);

            StringBuilder sb = new StringBuilder();
            switch (cboHanjaSearchItem.SelectedIndex)
            {
                case 1/* 음독 */:
                    sb.Append(" SOUND_READ LIKE ");
                    break;
                case 2/* 훈독 */:
                    sb.Append(" MEAN_READ LIKE ");
                    break;
                case 3/* 뜻 */:
                    sb.Append(" TRANSLATION LIKE ");
                    break;
                default/* 한자 */:
                    sb.Append(" CHARACTER LIKE ");
                    break;
            }

            sb.Append(@"""%");
            sb.Append(searchWord);
            sb.Append(@"%""");

            FillHanjaData(sb.ToString());
        }

        private void dataVocabularyGridView_RowPostPaint(object sender, DataGridViewRowPostPaintEventArgs e)
        {
            Rectangle rect = new Rectangle(e.RowBounds.Location.X,
                                           e.RowBounds.Location.Y,
                                           dataVocabularyGridView.RowHeadersWidth - 4,
                                           e.RowBounds.Height);

            TextRenderer.DrawText(e.Graphics,
                                  (e.RowIndex + 1).ToString(),
                                  dataVocabularyGridView.RowHeadersDefaultCellStyle.Font,
                                  rect,
                                  dataVocabularyGridView.RowHeadersDefaultCellStyle.ForeColor,
                                  TextFormatFlags.VerticalCenter | TextFormatFlags.Right);
        }

        private void dataHanjaGridView_RowPostPaint(object sender, DataGridViewRowPostPaintEventArgs e)
        {
            Rectangle rect = new Rectangle(e.RowBounds.Location.X,
                                           e.RowBounds.Location.Y,
                                           dataHanjaGridView.RowHeadersWidth - 4,
                                           e.RowBounds.Height);

            TextRenderer.DrawText(e.Graphics,
                                  (e.RowIndex + 1).ToString(),
                                  dataHanjaGridView.RowHeadersDefaultCellStyle.Font,
                                  rect,
                                  dataHanjaGridView.RowHeadersDefaultCellStyle.ForeColor,
                                  TextFormatFlags.VerticalCenter | TextFormatFlags.Right);
        }

        private void dataVocabularyGridView_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            // 현재 선택된 행을 얻는다.
            DataGridViewSelectedRowCollection rc = dataVocabularyGridView.SelectedRows;

            Debug.Assert(rc.Count == 1);
            if (rc.Count != 1)
                return;

            frmVocabulary form = new frmVocabulary();

            // @@@@@ 급수, 품사
            form.EditMode = true;
            form.DbConnection = mDbConnection;
            form.idx = long.Parse(rc[0].Cells[0].Value.ToString());
            form.Vocabulary = rc[0].Cells[1].Value.ToString();
            form.VocabularyGana = rc[0].Cells[2].Value.ToString();
            form.VocabularyTranslation = rc[0].Cells[3].Value.ToString();

            if (form.ShowDialog() == DialogResult.OK)
            {
                rc[0].Cells[1].Value = form.Vocabulary;
                rc[0].Cells[2].Value = form.VocabularyGana;
                rc[0].Cells[3].Value = form.VocabularyTranslation;
            }

            rc[0].Cells[6].Value = "-";

            // 예문 카운트를 구하여 업데이트 한다.
            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = string.Format("SELECT COUNT(*) AS EXAMPLE_COUNT FROM TBL_VOCABULARY_EXAMPLE_MAPP WHERE V_IDX={0}", long.Parse(rc[0].Cells[0].Value.ToString()));

                SQLiteCommand cmd = new SQLiteCommand(strSQL, mDbConnection);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    if (reader.HasRows == true && reader.Read() == true)
                    {
                        int nCount = reader.GetInt32(0/*EXAMPLE_COUNT*/);
                        if (nCount > 0)
                            rc[0].Cells[6].Value = nCount;
                        else
                            rc[0].Cells[6].Value = "0";
                    }
                }
            }
            catch (SQLiteException)
            {
            }

            form.Dispose();
        }

        private void dataHanjaGridView_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            // 현재 선택된 행을 얻는다.
            DataGridViewSelectedRowCollection rc = dataHanjaGridView.SelectedRows;

            if (rc.Count != 1)
                return;

            frmHanja form = new frmHanja();

            form.EditMode       = true;
            form.DbConnection   = mDbConnection;
            form.idx            = long.Parse(rc[0].Cells[0].Value.ToString());
            form.Character      = rc[0].Cells[1].Value.ToString();
            form.SoundRead      = rc[0].Cells[2].Value.ToString();
            form.MeanRead       = rc[0].Cells[3].Value.ToString();
            form.Translation    = rc[0].Cells[4].Value.ToString();

            if (form.ShowDialog() == DialogResult.OK)
            {
                rc[0].Cells[1].Value = form.Character;
                rc[0].Cells[2].Value = form.SoundRead;
                rc[0].Cells[3].Value = form.MeanRead;
                rc[0].Cells[4].Value = form.Translation;
            }

            form.Dispose();
        }

        private void btnWordAdd_Click(object sender, EventArgs e)
        {
            frmVocabulary form = new frmVocabulary();
            
            form.DbConnection   = mDbConnection;
            form.EditMode       = false;

            if (form.ShowDialog() == DialogResult.OK)
                FillData();
        }

        private void btnHanjaAdd_Click(object sender, EventArgs e)
        {
            frmHanja form = new frmHanja();

            form.EditMode       = false;
            form.DbConnection   = mDbConnection;

            if (form.ShowDialog() == DialogResult.OK)
                FillData();
        }

        #endregion

        #region 데이터베이스 처리

        private bool ConnectDB(out string errorMessage)
        {
            errorMessage = string.Empty;

            // DB가 이미 오픈되어 있는 경우는 먼저 닫는다.
            if (this.mDbConnection != null)
                DisconnectDB();

            Debug.Assert(this.mDbConnection == null);

            try
            {
                if (File.Exists(DB_FILE_NAME) == false)
                {
                    errorMessage = string.Format("{0} 파일을 찾을 수 업습니다.", DB_FILE_NAME);
                    return false;
                }

                this.mDbConnection = new SQLiteConnection(string.Format("Data Source={0}", DB_FILE_NAME));
                this.mDbConnection.Open();

                List<string> tableList = new List<string>();
                using (SQLiteCommand cmd = mDbConnection.CreateCommand())
                {
                    cmd.CommandText = "  SELECT name FROM ( " +
                                      "                       SELECT * " + 
                                      "                         FROM sqlite_master" + 
                                      "                    UNION ALL" +
                                      "                       SELECT * " +
                                      "                         FROM sqlite_temp_master" + 
                                      "                   ) " +
                                      "   WHERE type='table' " +
                                      "ORDER BY name";

                    using (SQLiteDataReader dataReader = cmd.ExecuteReader())
                    {
                        while (dataReader.HasRows && dataReader.Read())
                            tableList.Add(dataReader.GetString(0));
                    }
                }

                List<string> checkTableList = new List<String>();
                checkTableList.Add("TBL_CODE");
                checkTableList.Add("TBL_HANJA");
                checkTableList.Add("TBL_VOCABULARY");
                checkTableList.Add("TBL_VOCABULARY_EXAMPLE");
                checkTableList.Add("TBL_VOCABULARY_EXAMPLE_MAPP");
                checkTableList.Add("TBL_VOCABULARY_WORD_CLASS_MAPP");
                checkTableList.Add("TBL_VOCABULARY_JLPT_CLASS_MAPP");

                foreach (string tableName in checkTableList) 
                {
                    if (tableList.Contains(tableName) == false)
                    {
                        errorMessage = string.Format("{0} 파일에 '{1}' 테이블이 존재하지 않습니다.", DB_FILE_NAME, tableName);
                        return false;
                    }
                }
            }
            catch (SQLiteException e)
            {
                errorMessage = e.Message;
                return false;
            }
            catch (Exception e)
            {
                errorMessage = e.Message;
                return false;
            }

            return true;
        }

        private bool DisconnectDB()
        {
            if (this.mDbConnection != null)
                this.mDbConnection.Close();

            this.mDbConnection = null;

            return true;
        }

        private void FillData()
        {
            FillVocabularyData(string.Empty);
            FillHanjaData(string.Empty);
        }

        private void FillVocabularyData(string sqlWhere)
        {
            Debug.Assert(this.mDbConnection != null);

            // 전체 행을 삭제합니다.
            dataVocabularyGridView.Rows.Clear();
            
            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = "SELECT A.IDX, A.VOCABULARY, A.VOCABULARY_GANA, A.VOCABULARY_TRANSLATION, A.USE_YN, " +
                                "       (   SELECT COUNT(*) " +
                                "             FROM TBL_VOCABULARY_EXAMPLE_MAPP AA, " +
                                "                  TBL_VOCABULARY_EXAMPLE BB " +
                                "            WHERE AA.V_IDX = A.IDX " +
                                "              AND AA.E_IDX = BB.IDX ) EXAMPLE_COUNT, " +
                                "       IFNULL((   SELECT GROUP_CONCAT(AA.CODE_ID, ',') " +
                                "                    FROM TBL_VOCABULARY_WORD_CLASS_MAPP AA " +
                                "                   WHERE AA.V_IDX = A.IDX" +
                                "                GROUP BY AA.V_IDX ), '') WORD_CLASS_CODE, " +
                                "       IFNULL((   SELECT GROUP_CONCAT(BB.CODE_NAME, ',') " +
                                "                    FROM TBL_VOCABULARY_WORD_CLASS_MAPP AA, " +
                                "                         TBL_CODE BB " +
                                "                   WHERE AA.V_IDX = A.IDX" +
                                "                     AND AA.CODE_ID = BB.CODE_ID " +
                                "                     AND BB.CODE_GRP_ID = 'W01' " +
                                "                GROUP BY AA.V_IDX ), '') WORD_CLASS_TEXT, " +
                                "       IFNULL((   SELECT GROUP_CONCAT(AA.CODE_ID, ',') " +
                                "                    FROM TBL_VOCABULARY_JLPT_CLASS_MAPP AA " +
                                "                   WHERE AA.V_IDX = A.IDX" +
                                "                GROUP BY AA.V_IDX ), '') JLPT_CLASS_CODE, " +
                                "       IFNULL((   SELECT GROUP_CONCAT(BB.CODE_NAME, ',') " +
                                "                    FROM TBL_VOCABULARY_JLPT_CLASS_MAPP AA, " +
                                "                         TBL_CODE BB " +
                                "                   WHERE AA.V_IDX = A.IDX" +
                                "                     AND AA.CODE_ID = BB.CODE_ID " +
                                "                     AND BB.CODE_GRP_ID = 'J01' " +
                                "                GROUP BY AA.V_IDX ), '') JLPT_CLASS_TEXT " +
                                "  FROM TBL_VOCABULARY A ";

                if (string.IsNullOrEmpty(sqlWhere) == false)
                    strSQL += " AND " + sqlWhere;

                SQLiteCommand cmd = new SQLiteCommand(strSQL, mDbConnection);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    while (reader.HasRows == true && reader.Read() == true)
                    {
                        dataVocabularyGridView.Rows.Add(reader.GetInt32(0/* IDX */), 
                                                        reader.GetString(1/* VOCABULARY */), 
                                                        reader.GetString(2/* VOCABULARY_GANA */), 
                                                        reader.GetString(3/* VOCABULARY_TRANSLATION */),
                                                        reader.GetString(7/* WORD_CLASS_TEXT */),
                                                        reader.GetString(9/* JLPT_CLASS_TEXT */),
                                                        reader.GetInt32(5/* EXAMPLE_COUNT */),
                                                        reader.GetString(4/* USE_YN */),
                                                        "",
                                                        reader.GetString(6/* WORD_CLASS_CODE */),
                                                        reader.GetString(8/* JLPT_CLASS_CODE */));
                    }
                }
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message);
            }
        }

        private void FillHanjaData(string sqlWhere)
        {
            Debug.Assert(this.mDbConnection != null);

            // 전체 행을 삭제합니다.
            dataHanjaGridView.Rows.Clear();

            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = "SELECT IDX, CHARACTER, SOUND_READ, MEAN_READ, TRANSLATION FROM TBL_HANJA ";

                if (string.IsNullOrEmpty(sqlWhere) == false)
                    strSQL += " WHERE " + sqlWhere;

                SQLiteCommand cmd = new SQLiteCommand(strSQL, mDbConnection);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    while (reader.HasRows == true && reader.Read() == true)
                    {
                        dataHanjaGridView.Rows.Add(reader.GetInt32(0/*IDX*/), 
                                                   reader.GetString(1/*CHARACTER*/), 
                                                   reader.GetString(2/*SOUND_READ*/), 
                                                   reader.GetString(3/*MEAN_READ*/), 
                                                   reader.GetString(4/*TRANSLATION*/));
                    }
                }
            }
            catch (SQLiteException e)
            {
                MessageBox.Show(e.Message);
            }
        }

        #endregion

        private void txtVocabularySearchWord_TextChanged(object sender, EventArgs e)
        {
            if (txtVocabularySearchWord.Text.Trim().Length == 0)
                btnVocabularySearch.Enabled = false;
            else
                btnVocabularySearch.Enabled = true;
        }

        private void txtVocabularySearchWord_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == 13/* 엔터 */)
            {
                btnVocabularySearch.PerformClick();
                e.Handled = true;
            }
        }
        
        private void btnVocabularySearch_Click(object sender, EventArgs e)
        {
            string searchWord = txtVocabularySearchWord.Text.Trim();
            Debug.Assert(string.IsNullOrEmpty(searchWord) == false);

            StringBuilder sb = new StringBuilder();
            switch (cboWordSearchItem.SelectedIndex)
            {
                case 1/* 히라가나/가타가나 */:
                    sb.Append(" VOCABULARY_GANA LIKE ");
                    break;
                case 2/* 설명 */:
                    sb.Append(" VOCABULARY_TRANSLATION LIKE ");
                    break;
                default/* 단어 */:
                    sb.Append(" VOCABULARY LIKE ");
                    break;
            }

            sb.Append(@"""%");
            sb.Append(searchWord);
            sb.Append(@"%""");

            FillVocabularyData(sb.ToString());
        }

        private void btnVocabularyShowAll_Click(object sender, EventArgs e)
        {
            FillVocabularyData(string.Empty);
        }

        private void dataVocabularyGridView_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Delete)
            {
                DataGridView dataGridView = (DataGridView)sender;
                if (dataGridView.Rows.GetRowCount(DataGridViewElementStates.Selected) == 1)
                {
                    if (MessageBox.Show("선택하신 데이터의 삭제여부 플래그를 'N'로 설정하시겠습니까?\n(실제 데이터는 삭제되지 않습니다)", "삭제여부 플래그 설정", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                    {
                        using (SQLiteCommand cmd = mDbConnection.CreateCommand())
                        {
                            cmd.CommandText = string.Format("UPDATE TBL_VOCABULARY SET USE_YN='N' WHERE idx = {0};", dataGridView.SelectedRows[0].Cells[0].Value);
                            cmd.ExecuteNonQuery();
                        }

                        dataGridView.SelectedRows[0].Cells[7/* 사용유무 */].Value = "N";

                        e.Handled = true;
                    }
                }
                else if (dataGridView.Rows.GetRowCount(DataGridViewElementStates.Selected) > 1)
                {
                    MessageBox.Show("단어 데이터를 삭제하시려면 하나의 행만 선택하여 주세요!");
                    e.Handled = true;
                }
            }
        }

        private void btnHanjaDataAnalyser_Click(object sender, EventArgs e)
        {
            // 한자가 선택되었는지 확인한다.
            DataGridViewSelectedRowCollection rc = dataHanjaGridView.SelectedRows;
            if (rc.Count <= 0)
            {
                MessageBox.Show("분석작업을 진행 할 선택된 한자가 없습니다!");
                return;
            }

            int nIndex = 0;
            string btnSourceText = btnHanjaDataAnalyser.Text;

            foreach (DataGridViewRow row in rc)
            {
                ++nIndex;
                btnHanjaDataAnalyser.Text = string.Format("{0}[{1}/{2}]", btnSourceText, nIndex, rc.Count);
                btnHanjaDataAnalyser.Refresh();

                String strSourceHanja = row.Cells[1].Value.ToString();
                String strSourceSoundRead = row.Cells[2].Value.ToString();
                String strSourceMeanRead = row.Cells[3].Value.ToString();
                String strSourceTranslation = row.Cells[4].Value.ToString();

                string htmlDocumentContent = LoadWebPage(string.Format("http://jpdic.naver.com/search.nhn?query={0}", strSourceHanja));

                // HTML 데이터를 파싱한다.
                HtmlAgilityPack.HtmlDocument htmlDoc = new HtmlAgilityPack.HtmlDocument();
                htmlDoc.LoadHtml(htmlDocumentContent);

                HtmlAgilityPack.HtmlNode bodyNode = htmlDoc.DocumentNode.SelectSingleNode("//body");
                HtmlAgilityPack.HtmlNodeCollection srchBoxNodeList = bodyNode.SelectNodes("//div[@class='srch_box']");
                foreach (HtmlAgilityPack.HtmlNode srchBoxNode in srchBoxNodeList)
                {
                    var characterNodeList = srchBoxNode.SelectSingleNode("//div[@class='srch_top']//a//span[@class='jp']");
                    if (characterNodeList != null && characterNodeList.InnerText == strSourceHanja)
                    {
                        String strMeadRead = "";
                        String strSoundRead = "";
                        String strTranslation = "";

                        // 음독, 훈독이 있는 노드
                        var htmlNodes = srchBoxNode.SelectNodes("./dl[@class='top_dn']/*");
                        if (htmlNodes != null)
                        {
                            for (int index = 0; index < htmlNodes.Count; ++index)
                            {
                                HtmlAgilityPack.HtmlNode htmlNode = htmlNodes.ElementAt(index);
                                if (htmlNode.InnerText.Equals("음독"))
                                {
                                    HtmlAgilityPack.HtmlNode htmlNextNode = htmlNodes.ElementAt(index + 1);
                                    if (htmlNextNode != null)
                                    {
                                        string text = htmlNextNode.InnerText;
                                        int pos1 = text.IndexOf('|');
                                        if (pos1 != -1)
                                            text = text.Substring(0, pos1 - 1);

                                        strSoundRead = text.Trim();
                                    }
                                }
                                else if (htmlNode.InnerText.Equals("훈독"))
                                {
                                    HtmlAgilityPack.HtmlNode htmlNextNode = htmlNodes.ElementAt(index + 1);
                                    if (htmlNextNode != null)
                                    {
                                        string text = htmlNextNode.InnerText;
                                        int pos2 = text.IndexOf('|');
                                        if (pos2 != -1)
                                            text = text.Substring(0, pos2 - 1);

                                        strMeadRead = text.Trim();
                                    }
                                }
                            }

                            // 뜻이 있는 노드
                            var translationNode = srchBoxNode.SelectSingleNode("//dl[@class='top_dn top_dn_v2']/dd[@class='ft_col3']/span[@class='ft_col3']");
                            if (translationNode != null)
                                strTranslation = translationNode.InnerText;
                        }

                        // 값을 비교한다.
                        if (strSourceSoundRead == strSoundRead && strSourceMeanRead == strMeadRead && strSourceTranslation == strTranslation)
                            row.DefaultCellStyle.BackColor = Color.Yellow;
                        else
                            row.DefaultCellStyle.BackColor = Color.Red;

                        if (strSoundRead.Length != 0 || strMeadRead.Length != 0 || strTranslation.Length != 0)
                            break;
                    }
                }
            }

            btnHanjaDataAnalyser.Text = btnSourceText;

            MessageBox.Show("파싱이 완료되었습니다.");
        }

        private void btnHanjaExistCheck_Click(object sender, EventArgs e)
        {
            // 한자가 선택되었는지 확인한다.
            DataGridViewSelectedRowCollection rc = dataVocabularyGridView.SelectedRows;
            if (rc.Count <= 0)
            {
                MessageBox.Show("분석작업을 진행 할 선택된 단어가 없습니다!");
                return;
            }

            int nIndex = 0;
            string btnSourceText = btnHanjaExistCheck.Text;
            string japanGana = "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをんがぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽきゃきゅきょしゃしゅしょちゃちゅちょにゃにゅにょひゃひゅひょみゃみゅみょりゃりゅりょぎゃぎゅぎょじゃじゅじょびゃびゅびょぴゃぴゅぴょアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポキャキュキョシャシュショチャチュチョニャニュニョヒャヒュヒョミャミュミョリャリュリョギャギュギョジャジュジョビャビュビョピャピュピョ";

            foreach (DataGridViewRow row in rc)
            {
                ++nIndex;
                btnHanjaExistCheck.Text = string.Format("{0}[{1}/{2}]", btnSourceText, nIndex, rc.Count);
                btnHanjaExistCheck.Refresh();

                String strVocabulary = row.Cells[1].Value.ToString();
                char[] caVocabulary = strVocabulary.ToCharArray();

                using (SQLiteCommand cmd = mDbConnection.CreateCommand())
                {
                    StringBuilder sbSQL = new StringBuilder();
                    sbSQL.Append("SELECT COUNT(*) FROM TBL_HANJA WHERE CHARACTER IN (");

                    int nCheckHanjaCount = 0;
                    Boolean isFirstOne = true;
                    foreach (char c in caVocabulary)
                    {
                        if (japanGana.IndexOf(c) == -1)
                        {
                            if (isFirstOne == false)
                                sbSQL.Append(", ");

                            ++nCheckHanjaCount;
                            isFirstOne = false;
                            sbSQL.Append("'").Append(c).Append("'");
                        }
                    }
                    sbSQL.Append(")");

                    if (nCheckHanjaCount == 0)
                    {
                        row.DefaultCellStyle.BackColor = Color.Yellow;
                        row.Cells[8].Value = "일치";
                    }
                    else
                    {
                        cmd.CommandText = sbSQL.ToString();
                        using (SQLiteDataReader reader = cmd.ExecuteReader())
                        {
                            if (reader.HasRows == true && reader.Read() == true)
                            {
                                int nHanjaCount = reader.GetInt32(0/* COUNT */);
                                if (nHanjaCount == nCheckHanjaCount)
                                {
                                    row.DefaultCellStyle.BackColor = Color.Yellow;
                                    row.Cells[8].Value = "일치";
                                }
                                else
                                {
                                    row.DefaultCellStyle.BackColor = Color.Red;
                                    row.Cells[8].Value = string.Format("불일치({0}/{1})", nHanjaCount, nCheckHanjaCount);
                                }
                            }
                            else
                            {
                                row.DefaultCellStyle.BackColor = Color.Red;
                                row.Cells[8].Value = "파싱 실패";
                            }
                        }
                    }
                }
            }

            btnHanjaExistCheck.Text = btnSourceText;

            MessageBox.Show("파싱이 완료되었습니다.");
        }

        private void btnAddPossibleExampleCountCheck_Click(object sender, EventArgs e)
        {
            // 한자가 선택되었는지 확인한다.
            DataGridViewSelectedRowCollection rc = dataVocabularyGridView.SelectedRows;
            if (rc.Count <= 0)
            {
                MessageBox.Show("분석작업을 진행 할 선택된 단어가 없습니다!");
                return;
            }

            int nIndex = 0;
            string btnSourceText = btnAddPossibleExampleCountCheck.Text;

            foreach (DataGridViewRow row in rc)
            {
                ++nIndex;
                btnAddPossibleExampleCountCheck.Text = string.Format("{0}[{1}/{2}]", btnSourceText, nIndex, rc.Count);
                btnAddPossibleExampleCountCheck.Refresh();

                long idx = long.Parse(row.Cells[0].Value.ToString());
                String strVocabulary = row.Cells[1].Value.ToString();

                using (SQLiteCommand cmd = mDbConnection.CreateCommand())
                {
                    StringBuilder sbSQL = new StringBuilder();
                    sbSQL.Append("SELECT COUNT(*) ")
                         .Append("  FROM TBL_VOCABULARY_EXAMPLE A ")
                         .Append(" WHERE A.VOCABULARY LIKE '%").Append(strVocabulary.Trim()).Append("%'")
                         .Append("   AND A.IDX NOT IN ( SELECT AA.E_IDX ")
                         .Append("                        FROM TBL_VOCABULARY_EXAMPLE_MAPP AA ")
                         .Append("                       WHERE AA.V_IDX = ").Append(idx)
                         .Append("                    ) ");

                    cmd.CommandText = sbSQL.ToString();
                    using (SQLiteDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.HasRows == true && reader.Read() == true)
                        {
                            int nCount = reader.GetInt32(0/* COUNT */);
                            if (nCount == 0)
                            {
                                row.DefaultCellStyle.BackColor = Color.Yellow;
                                row.Cells[8].Value = "없음";
                            }
                            else
                            {
                                row.DefaultCellStyle.BackColor = Color.Red;
                                row.Cells[8].Value = nCount.ToString();
                            }
                        }
                        else
                        {
                            row.DefaultCellStyle.BackColor = Color.Red;
                            row.Cells[8].Value = "파싱 실패";
                        }
                    }
                }
            }

            btnAddPossibleExampleCountCheck.Text = btnSourceText;

            MessageBox.Show("파싱이 완료되었습니다.");
        }

        private void btnExtractVocabulary_Click(object sender, EventArgs e)
        {
            using (SQLiteTransaction tran = mDbConnection.BeginTransaction())
            {
                // 테이블을 삭제한다.
                using (SQLiteCommand cmd = mDbConnection.CreateCommand())
                {
                    StringBuilder sbSQL = new StringBuilder();
                    sbSQL.Append(" DROP TABLE IF EXISTS TBL_EXTRACT_VOCABULARY ");
                    cmd.CommandText = sbSQL.ToString();
                    cmd.ExecuteNonQuery();
                }

                // 테이블을 추가한다.
                using (SQLiteCommand cmd = mDbConnection.CreateCommand())
                {
                    StringBuilder sbSQL = new StringBuilder();
                    sbSQL.Append(" CREATE TABLE TBL_EXTRACT_VOCABULARY ( ")
                         .Append("      IDX                     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, ")
                         .Append("      VOCABULARY              VARCHAR(50), ")
                         .Append("      VOCABULARY_GANA         VARCHAR(50), ")
                         .Append("      VOCABULARY_TRANSLATION  TEXT, ")
                         .Append("      WORD_CLASS              VARCHAR(50), ")
                         .Append("      JLPT_CLASS              VARCHAR(50) ")
                         .Append(" ) ");

                    cmd.CommandText = sbSQL.ToString();
                    cmd.ExecuteNonQuery();
                }

                // N1~N5 단어를 추출한다.
                int totCount = 73 + 60 + 35 + 24 + 14;
                string btnSourceText = btnExtractVocabulary.Text;

                ExtractVocabulary(73, 1, "N1", btnSourceText, totCount, 0);
                ExtractVocabulary(60, 2, "N2", btnSourceText, totCount, 73);
                ExtractVocabulary(35, 3, "N3", btnSourceText, totCount, 73 + 60);
                ExtractVocabulary(24, 4, "N4", btnSourceText, totCount, 73 + 60 + 35);
                ExtractVocabulary(14, 5, "N5", btnSourceText, totCount, 73 + 60 + 35 + 24);

                btnExtractVocabulary.Text = btnSourceText;

                tran.Commit();
            }

            MessageBox.Show("작업이 완료되었습니다.");
        }

        private void ExtractVocabulary(int loopCount, int jlptLevel, string jlptLevelText, string btnSourceText, int totCount, int beginCount)
        {
            for (var index = 1; index <= loopCount; ++index)
            {
                ++beginCount;
                btnExtractVocabulary.Text = string.Format("{0}[{1}/{2}]", btnSourceText, beginCount, totCount);
                btnExtractVocabulary.Refresh();

                // 네이버에서 데이터를 읽어온다.
                string htmlDocumentContent = LoadWebPage(string.Format("http://jpdic.naver.com/jlpt/level-{0}/parts-0/p{1}.nhn", jlptLevel, index));

                // 읽어온 데이터를 파싱한다.
                HtmlAgilityPack.HtmlDocument htmlDoc = new HtmlAgilityPack.HtmlDocument();
                htmlDoc.LoadHtml(htmlDocumentContent);

                HtmlAgilityPack.HtmlNodeCollection jlptNodeList = htmlDoc.DocumentNode.SelectNodes("//div[@class='jlpt_lst']/ul[@class='lst']/li");
                foreach (HtmlAgilityPack.HtmlNode jlptNode in jlptNodeList)
                {
                    String strVocabulary = "";
                    String strVocabularyGana = "";
                    String strVocabularyTranslation = "";
                    String strWordClass = "";
                    String strJlptClass = jlptLevelText;

                    var characterNode = jlptNode.SelectSingleNode("./span[@class='jp']");
                    var translationNode = jlptNode.SelectSingleNode("./span[@class='bot_txt']");

                    strVocabularyGana = characterNode.InnerText;
                    int chpos = strVocabularyGana.IndexOf('[');
                    if (chpos != -1)
                    {
                        strVocabulary = strVocabularyGana.Substring(chpos + 1).Replace("[", "").Replace("]", "");
                        strVocabularyGana = strVocabularyGana.Substring(0, chpos);

                    }

                    strVocabularyTranslation = translationNode.InnerText;
                    chpos = strVocabularyTranslation.IndexOf(']');
                    if (chpos != -1)
                    {
                        strWordClass = strVocabularyTranslation.Substring(0, chpos).Replace("[", "").Replace("]", "");
                        strVocabularyTranslation = strVocabularyTranslation.Substring(chpos + 1).Replace("→", "");
                    }

                    using (SQLiteCommand cmd = mDbConnection.CreateCommand())
                    {
                        cmd.CommandText = "INSERT INTO TBL_EXTRACT_VOCABULARY (VOCABULARY, VOCABULARY_GANA, VOCABULARY_TRANSLATION, WORD_CLASS, JLPT_CLASS) VALUES (?,?,?,?,?);";
                        SQLiteParameter param1 = new SQLiteParameter();
                        SQLiteParameter param2 = new SQLiteParameter();
                        SQLiteParameter param3 = new SQLiteParameter();
                        SQLiteParameter param4 = new SQLiteParameter();
                        SQLiteParameter param5 = new SQLiteParameter();
                        cmd.Parameters.Add(param1);
                        cmd.Parameters.Add(param2);
                        cmd.Parameters.Add(param3);
                        cmd.Parameters.Add(param4);
                        cmd.Parameters.Add(param5);

                        param1.Value = strVocabulary;
                        param2.Value = strVocabularyGana;
                        param3.Value = strVocabularyTranslation;
                        param4.Value = strWordClass;
                        param5.Value = strJlptClass;
                        cmd.ExecuteNonQuery();
                    }
                }
            }
        }

        private string LoadWebPage(string url)
        {
            WebClient wc = new WebClient();
            byte[] docBytes = wc.DownloadData(url);
            string encodeType = wc.ResponseHeaders["Content-Type"];

            string charsetKey = "charset";
            int pos = encodeType.IndexOf(charsetKey);

            Encoding currentEncoding = Encoding.Default;
            if (pos != -1)
            {
                pos = encodeType.IndexOf("=", pos + charsetKey.Length);
                if (pos != -1)
                {
                    string charset = encodeType.Substring(pos + 1);
                    currentEncoding = Encoding.GetEncoding(charset);
                }
            }

            return currentEncoding.GetString(docBytes);
        }
    }
}
