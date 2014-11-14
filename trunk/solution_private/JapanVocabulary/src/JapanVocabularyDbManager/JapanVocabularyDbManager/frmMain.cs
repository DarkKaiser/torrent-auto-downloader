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
        private static string DATA_FOLDER_NAME = ".";

        private SQLiteConnection _dbConn = null;

        public frmMain()
        {
            InitializeComponent();
        }

        #region 이벤트 핸들러

        private void frmMain_Load(object sender, EventArgs e)
        {
            // 프로그램을 초기화합니다.
            cboWordSearchItem.Items.Add("단어");
            cboWordSearchItem.Items.Add("일본어");
            cboWordSearchItem.Items.Add("설명");
            cboWordSearchItem.SelectedIndex = 2;

            cboHanjaSearchItem.Items.Add("한자");
            cboHanjaSearchItem.Items.Add("음독");
            cboHanjaSearchItem.Items.Add("훈독");
            cboHanjaSearchItem.Items.Add("뜻");
            cboHanjaSearchItem.SelectedIndex = 3;

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

        private void txtWordSearchWord_TextChanged(object sender, EventArgs e)
        {
            if (txtWordSearchWord.Text.Trim().Length == 0)
                btnWordSearch.Enabled = false;
            else
                btnWordSearch.Enabled = true;
        }
        
        private void txtHanjaSearchWord_TextChanged(object sender, EventArgs e)
        {
            if (txtHanjaSearchWord.Text.Trim().Length == 0)
                btnHanjaSearch.Enabled = false;
            else
                btnHanjaSearch.Enabled = true;
        }

        private void btnWordAll_Click(object sender, EventArgs e)
        {
            FillVocabularyData(string.Empty);
        }

        private void btnHanjaAll_Click(object sender, EventArgs e)
        {
            FillHanjaData(string.Empty);
        }

        private void dataWordGridView_UserDeletingRow(object sender, DataGridViewRowCancelEventArgs e)
        {
            if (MessageBox.Show("선택하신 데이터를 삭제하시겠습니까?", "삭제", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.No)
                e.Cancel = true;
        }
       
        private void dataHanjaGridView_UserDeletingRow(object sender, DataGridViewRowCancelEventArgs e)
        {
            if (MessageBox.Show("선택하신 데이터를 삭제하시겠습니까?", "삭제", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.No)
                e.Cancel = true;
        }

        private void dataWordGridView_UserDeletedRow(object sender, DataGridViewRowEventArgs e)
        {
            using (SQLiteCommand cmd = _dbConn.CreateCommand())
            {
                cmd.CommandText = string.Format("DELETE FROM TBL_VOCABULARY WHERE idx = {0};", e.Row.Cells[0].Value);
                cmd.ExecuteNonQuery();
            }
        }

        private void dataHanjaGridView_UserDeletedRow(object sender, DataGridViewRowEventArgs e)
        {
            using (SQLiteCommand cmd = _dbConn.CreateCommand())
            {
                cmd.CommandText = string.Format("DELETE FROM TBL_HANJA WHERE idx = {0};", e.Row.Cells[0].Value);
                cmd.ExecuteNonQuery();
            }
        }

        private void btnWordSearch_Click(object sender, EventArgs e)
        {
            string searchWord = txtWordSearchWord.Text.Trim();
            Debug.Assert(string.IsNullOrEmpty(searchWord) == false);

            StringBuilder sb = new StringBuilder();
            switch (cboWordSearchItem.SelectedIndex)
            {
                case 1:
                    sb.Append("VOCABULARY_GANA LIKE ");
                    break;
                case 2:
                    sb.Append("VOCABULARY_TRANSLATION LIKE ");
                    break;
                default:
                    sb.Append("VOCABULARY LIKE ");
                    break;
            }

            sb.Append(@"""%");
            sb.Append(searchWord);
            sb.Append(@"%""");

            FillVocabularyData(sb.ToString());
        }

        private void btnHanjaSearch_Click(object sender, EventArgs e)
        {
            string searchWord = txtHanjaSearchWord.Text.Trim();
            Debug.Assert(string.IsNullOrEmpty(searchWord) == false);

            StringBuilder sb = new StringBuilder();
            switch (cboHanjaSearchItem.SelectedIndex)
            {
                case 1:
                    sb.Append("SOUND_READ LIKE ");
                    break;
                case 2:
                    sb.Append("MEAN_READ LIKE ");
                    break;
                case 3:
                    sb.Append("TRANSLATION LIKE ");
                    break;
                default:
                    sb.Append("CHARACTER LIKE ");
                    break;
            }

            sb.Append(@"""%");
            sb.Append(searchWord);
            sb.Append(@"%""");

            FillHanjaData(sb.ToString());
        }

        private void dataWordGridView_RowPostPaint(object sender, DataGridViewRowPostPaintEventArgs e)
        {
            Rectangle rect = new Rectangle(e.RowBounds.Location.X,
                                           e.RowBounds.Location.Y,
                                           dataWordGridView.RowHeadersWidth - 4,
                                           e.RowBounds.Height);

            TextRenderer.DrawText(e.Graphics,
                                  (e.RowIndex + 1).ToString(),
                                  dataWordGridView.RowHeadersDefaultCellStyle.Font,
                                  rect,
                                  dataWordGridView.RowHeadersDefaultCellStyle.ForeColor,
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

        private void dataWordGridView_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            // 현재 선택된 행을 얻는다.
            DataGridViewSelectedRowCollection rc = dataWordGridView.SelectedRows;

            Debug.Assert(rc.Count == 1);
            if (rc.Count != 1)
                return;

            frmVocabulary form = new frmVocabulary();
            form.EditMode = true;
            form.idx = long.Parse(rc[0].Cells[0].Value.ToString());
            form.Vocabulary = rc[0].Cells[1].Value.ToString();
            form.VocabularyGana = rc[0].Cells[2].Value.ToString();
            form.VocabularyTranslation = rc[0].Cells[3].Value.ToString();
            form.DbConnection = _dbConn;

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

                SQLiteCommand cmd = new SQLiteCommand(strSQL, _dbConn);
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

        private void dataHanjaGridView_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
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
            form.DbConnection = _dbConn;

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

        private void btnWordAdd_Click(object sender, EventArgs e)
        {
            frmVocabulary form = new frmVocabulary();
            form.DbConnection = _dbConn;
            form.EditMode = false;

            if (form.ShowDialog() == DialogResult.OK)
                FillData();
        }

        private void btnHanjaAdd_Click(object sender, EventArgs e)
        {
            frmHanja form = new frmHanja();
            form.DbConnection = _dbConn;
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
            if (_dbConn != null)
                DisconnectDB();

            Debug.Assert(_dbConn == null);

            // Data 폴더가 존재하지 않는 경우 폴더를 생성한다.
            string dataPath = string.Format(@"{0}\{1}\", Directory.GetCurrentDirectory(), DATA_FOLDER_NAME);

            try
            {
                if (Directory.Exists(dataPath) == false)
                    Directory.CreateDirectory(dataPath);
            }
            catch (Exception e)
            {
                errorMessage = "데이터 폴더의 생성이 실패하였습니다.";
                return false;
            }

            // DB를 오픈한다.
            _dbConn = new SQLiteConnection(string.Format("Data Source={0}/{1}", DATA_FOLDER_NAME, DB_FILE_NAME));
            _dbConn.Open();

            // DB에 필요한 테이블이 존재하는지 확인한다.
            try
            {
                List<string> tableList = new List<string>();
                using (SQLiteCommand cmd = _dbConn.CreateCommand())
                {
                    cmd.CommandText = "  SELECT name FROM (" +
                                      "                       SELECT * " + 
                                      "                         FROM sqlite_master" + 
                                      "                    UNION ALL" +
                                      "                       SELECT * " +
                                      "                         FROM sqlite_temp_master" + 
                                      "                   )" +
                                      "   WHERE type='table' " +
                                      "ORDER BY name";

                    using (SQLiteDataReader dataReader = cmd.ExecuteReader())
                    {
                        while (dataReader.HasRows && dataReader.Read())
                            tableList.Add(dataReader.GetString(0));
                    }
                }

                if (tableList.Contains("TBL_HANJA") == false)
                {
                    errorMessage = "'TBL_HANJA' 테이블이 존재하지 않습니다.";
                    return false;
                }

                if (tableList.Contains("TBL_VOCABULARY") == false)
                {
                    errorMessage = "'TBL_VOCABULARY' 테이블이 존재하지 않습니다.";
                    return false;
                }
            }
            catch (SQLiteException e)
            {
                errorMessage = e.Message;
                return false;
            }

            return true;
        }

        private bool DisconnectDB()
        {
            if (this._dbConn != null)
                this._dbConn.Close();

            this._dbConn = null;

            return true;
        }

        private void FillData()
        {
            FillVocabularyData(string.Empty);
            FillHanjaData(string.Empty);
        }

        private void FillVocabularyData(string sqlWhere)
        {
            Debug.Assert(_dbConn != null);

            // 전체 행을 삭제합니다.
            dataWordGridView.Rows.Clear();
            
            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = "SELECT A.IDX, A.VOCABULARY, A.VOCABULARY_GANA, A.VOCABULARY_TRANSLATION, (SELECT COUNT(*) AS EXAMPLE_COUNT FROM TBL_VOCABULARY_EXAMPLE WHERE A.IDX = V_IDX) FROM TBL_VOCABULARY A";
                if (string.IsNullOrEmpty(sqlWhere) == false)
                    strSQL += " WHERE " + sqlWhere;

                SQLiteCommand cmd = new SQLiteCommand(strSQL, _dbConn);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    while (reader.HasRows == true && reader.Read() == true)
                    {
                        long nCount = reader.GetInt32(4/*EXAMPLE_COUNT*/);
                        if (nCount > 0)
                            dataWordGridView.Rows.Add(reader.GetInt32(0/*IDX*/), reader.GetString(1/*VOCABULARY*/), reader.GetString(2/*VOCABULARY_GANA*/), reader.GetString(3/*VOCABULARY_TRANSLATION*/), nCount);
                        else
                            dataWordGridView.Rows.Add(reader.GetInt32(0/*IDX*/), reader.GetString(1/*VOCABULARY*/), reader.GetString(2/*VOCABULARY_GANA*/), reader.GetString(3/*VOCABULARY_TRANSLATION*/));
                    }
                }
            }
            catch (SQLiteException e)
            {
            }
        }

        private void FillHanjaData(string sqlWhere)
        {
            Debug.Assert(_dbConn != null);

            // 전체 행을 삭제합니다.
            dataHanjaGridView.Rows.Clear();

            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = "SELECT IDX, CHARACTER, SOUND_READ, MEAN_READ, JLPT_CLASS, TRANSLATION FROM TBL_HANJA ";
                if (string.IsNullOrEmpty(sqlWhere) == false)
                    strSQL += " WHERE " + sqlWhere;

                SQLiteCommand cmd = new SQLiteCommand(strSQL, _dbConn);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    while (reader.HasRows == true && reader.Read() == true)
                    {
                        string strLevel = "";
                        int nLevel = reader.GetInt32(4/*JLPT_CLASS*/);
                        if (nLevel != 99)
                            strLevel = "N" + nLevel;

                        dataHanjaGridView.Rows.Add(reader.GetInt32(0/*IDX*/), reader.GetString(1/*CHARACTER*/), reader.GetString(2/*SOUND_READ*/), reader.GetString(3/*MEAN_READ*/), reader.GetString(5/*TRANSLATION*/), strLevel);
                    }
                }
            }
            catch (SQLiteException e)
            {
            }
        }

        #endregion

        private void txtWordSearchWord_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == 13)
            {
                btnWordSearch.PerformClick();
                e.Handled = true;
            }
        }
    }
}
