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

        private void dataHanjaGridView_UserDeletingRow(object sender, DataGridViewRowCancelEventArgs e)
        {
            if (MessageBox.Show("선택하신 데이터를 삭제하시겠습니까?", "삭제", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.No)
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

        /*@@@@@*/private void dataVocabularyGridView_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            // 현재 선택된 행을 얻는다.
            DataGridViewSelectedRowCollection rc = dataVocabularyGridView.SelectedRows;

            Debug.Assert(rc.Count == 1);
            if (rc.Count != 1)
                return;

            frmVocabulary form = new frmVocabulary();
            form.EditMode = true;
            form.idx = long.Parse(rc[0].Cells[0].Value.ToString());
            form.Vocabulary = rc[0].Cells[1].Value.ToString();
            form.VocabularyGana = rc[0].Cells[2].Value.ToString();
            form.VocabularyTranslation = rc[0].Cells[3].Value.ToString();
            form.DbConnection = mDbConnection;

            if (form.ShowDialog() == DialogResult.OK)
            {
                rc[0].Cells[1].Value = form.Vocabulary;
                rc[0].Cells[2].Value = form.VocabularyGana;
                rc[0].Cells[3].Value = form.VocabularyTranslation;
            }

            // 예문 카운트를 구하여 업데이트 한다.
            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = string.Format("SELECT COUNT(*) AS EXAMPLE_COUNT FROM TBL_VOCABULARY_EXAMPLE WHERE V_IDX={0}", long.Parse(rc[0].Cells[0].Value.ToString()));

                SQLiteCommand cmd = new SQLiteCommand(strSQL, mDbConnection);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    if (reader.HasRows == true && reader.Read() == true)
                    {
                        int nCount = reader.GetInt32(0/*EXAMPLE_COUNT*/);
                        if (nCount > 0)
                            rc[0].Cells[4].Value = nCount;
                        else
                            rc[0].Cells[4].Value = "";
                    }
                }
            }
            catch (SQLiteException ex)
            {
            }

            form.Dispose();
        }

        /*@@@@@*/private void dataHanjaGridView_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            // 현재 선택된 행을 얻는다.
            DataGridViewSelectedRowCollection rc = dataHanjaGridView.SelectedRows;

            Debug.Assert(rc.Count == 1);
            if (rc.Count != 1)
                return;

            frmHanja form = new frmHanja();
            form.EditMode = true;
            form.idx = long.Parse(rc[0].Cells[0].Value.ToString());
            form.Character = rc[0].Cells[1].Value.ToString();
            form.SoundRead = rc[0].Cells[2].Value.ToString();
            form.MeanRead = rc[0].Cells[3].Value.ToString();
            form.Translation = rc[0].Cells[4].Value.ToString();
            form.DbConnection = mDbConnection;

            string jlptLevel = rc[0].Cells[5].Value.ToString();
            if (jlptLevel == "")
                form.JLPTClass = 99;
            else
                form.JLPTClass = int.Parse(jlptLevel.Substring(1, 1));

            if (form.ShowDialog() == DialogResult.OK)
            {
                rc[0].Cells[1].Value = form.Character;
                rc[0].Cells[2].Value = form.SoundRead;
                rc[0].Cells[3].Value = form.MeanRead;
                rc[0].Cells[4].Value = form.Translation;

                string strLevel = "";
                if (form.JLPTClass != 99)
                    strLevel = "N" + form.JLPTClass;

                rc[0].Cells[5].Value = strLevel;
            }

            form.Dispose();
        }

        /*@@@@@*/private void btnWordAdd_Click(object sender, EventArgs e)
        {
            frmVocabulary form = new frmVocabulary();
            form.DbConnection = mDbConnection;
            form.EditMode = false;

            if (form.ShowDialog() == DialogResult.OK)
                FillData();
        }

        /*@@@@@*/private void btnHanjaAdd_Click(object sender, EventArgs e)
        {
            frmHanja form = new frmHanja();
            form.DbConnection = mDbConnection;
            form.EditMode = false;

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
                checkTableList.Add("TBL_HANJA_JLPT_CLASS_MAPP");

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
                string strSQL = "SELECT A.IDX, A.VOCABULARY, A.VOCABULARY_GANA, A.VOCABULARY_TRANSLATION, USE_YN, " +
                                "       ( SELECT COUNT(*) AS EXAMPLE_COUNT " +
                                "           FROM TBL_VOCABULARY_EXAMPLE_MAPP " +
                                "          WHERE A.IDX = V_IDX) " +
                                "  FROM TBL_VOCABULARY A ";

                if (string.IsNullOrEmpty(sqlWhere) == false)
                    strSQL += " WHERE " + sqlWhere;

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
                                                        reader.GetInt32(5/* EXAMPLE_COUNT */),
                                                        reader.GetString(4/* USE_YN */));
                    }
                }
            }
            catch (SQLiteException e)
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
                if (dataGridView.Rows.GetRowCount(DataGridViewElementStates.Selected) > 0)
                {
                    Debug.Assert(dataGridView.Rows.GetRowCount(DataGridViewElementStates.Selected) == 1);

                    if (MessageBox.Show("선택하신 데이터를 삭제하시겠습니까?", "삭제", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                    {
                        using (SQLiteCommand cmd = mDbConnection.CreateCommand())
                        {
                            cmd.CommandText = string.Format("UPDATE TBL_VOCABULARY SET USE_YN='N' WHERE idx = {0};", dataGridView.SelectedRows[0].Cells[0].Value);
                            cmd.ExecuteNonQuery();
                        }

                        dataGridView.SelectedRows[0].Cells[5/* 사용유무 */].Value = "N";

                        e.Handled = true;
                    }
                }
            }
        }
    }
}
