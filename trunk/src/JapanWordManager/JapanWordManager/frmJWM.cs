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

namespace JapanWordManager
{
    public partial class frmJWM : Form
    {
        private SQLiteConnection dbConn = null;

        private static string DB_FILE_NAME = "jv.db";
        private static string DATA_FOLDER_NAME = "Data";

        public frmJWM()
        {
            InitializeComponent();
        }

        #region 이벤트 핸들러

        private void frmJWM_Load(object sender, EventArgs e)
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

        private void btnWordSearch_Click(object sender, EventArgs e)
        {
            string searchWord = txtWordSearchWord.Text.Trim();
            Debug.Assert(string.IsNullOrEmpty(searchWord) == false);

            StringBuilder sb = new StringBuilder();
            switch (cboWordSearchItem.SelectedIndex)
            {
                case 1:
                    sb.Append("vocabulary_gana LIKE ");
                    break;
                case 2:
                    sb.Append("vocabulary_translation LIKE ");
                    break;
                default:
                    sb.Append("vocabulary LIKE ");
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
                    sb.Append("YmDok LIKE ");
                    break;
                case 2:
                    sb.Append("HunDok LIKE ");
                    break;
                case 3:
                    sb.Append("Description LIKE ");
                    break;
                default:
                    sb.Append("Word LIKE ");
                    break;
            }

            sb.Append(@"""%");
            sb.Append(searchWord);
            sb.Append(@"%""");

            FillHanjaData(sb.ToString());
        }

        private void dataWordGridView_UserDeletedRow(object sender, DataGridViewRowEventArgs e)
        {
            using (SQLiteCommand cmd = dbConn.CreateCommand())
            {
                cmd.CommandText = string.Format("DELETE FROM tbl_vocabulary WHERE idx = {0};", e.Row.Cells[0].Value);
                cmd.ExecuteNonQuery();
            }
        }

        private void dataHanjaGridView_UserDeletedRow(object sender, DataGridViewRowEventArgs e)
        {
            using (SQLiteCommand cmd = dbConn.CreateCommand())
            {
                cmd.CommandText = string.Format("DELETE FROM tbl_hanja WHERE idx = {0};", e.Row.Cells[0].Value);
                cmd.ExecuteNonQuery();
            }
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
            form.HiGaVocabulary = rc[0].Cells[2].Value.ToString();
            form.Description = rc[0].Cells[3].Value.ToString();
            form.DbConnection = dbConn;

            if (form.ShowDialog() == DialogResult.OK)
            {
                rc[0].Cells[1].Value = form.Vocabulary;
                rc[0].Cells[2].Value = form.HiGaVocabulary;
                rc[0].Cells[3].Value = form.Description;
            }
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
            form.Word = rc[0].Cells[1].Value.ToString();
            form.YmDok = rc[0].Cells[2].Value.ToString();
            form.HunDok = rc[0].Cells[3].Value.ToString();
            form.Description = rc[0].Cells[4].Value.ToString();
            form.DbConnection = dbConn;

            if (form.ShowDialog() == DialogResult.OK)
            {
                rc[0].Cells[1].Value = form.Word;
                rc[0].Cells[2].Value = form.YmDok;
                rc[0].Cells[3].Value = form.HunDok;
                rc[0].Cells[4].Value = form.Description;
            }
        }

        private void btnWordAdd_Click(object sender, EventArgs e)
        {
            frmVocabulary form = new frmVocabulary();
            form.DbConnection = dbConn;
            form.EditMode = false;

            if (form.ShowDialog() == DialogResult.OK)
                FillData();
        }

        private void btnHanjaAdd_Click(object sender, EventArgs e)
        {
            frmHanja form = new frmHanja();
            form.DbConnection = dbConn;
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
            if (dbConn != null)
                DisconnectDB();

            Debug.Assert(dbConn == null);

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
            dbConn = new SQLiteConnection(string.Format("Data Source={0}/{1}", DATA_FOLDER_NAME, DB_FILE_NAME));
            dbConn.Open();

            // DB에 테이블이 존재하지 않는 경우에 테이블을 생성한다.
            try
            {
                List<string> tableList = new List<string>();
                using (SQLiteCommand cmd = dbConn.CreateCommand())
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

                if (tableList.Contains("tbl_hanja") == false)
                {
                    using (SQLiteCommand cmd = dbConn.CreateCommand())
                    {
                        cmd.CommandText = "CREATE TABLE tbl_hanja (idx integer PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, Word varchar(10), YmDok varchar(50), HunDok varchar(50), Description text);";
                        cmd.ExecuteNonQuery();

                        cmd.CommandText = "CREATE UNIQUE INDEX tbl_hanja_Index01 ON tbl_hanja(idx);";
                        cmd.ExecuteNonQuery();

                        cmd.CommandText = "CREATE INDEX tbl_hanja_Index02 ON tbl_hanja(Word);";
                        cmd.ExecuteNonQuery();
                    }
                }

                if (tableList.Contains("tbl_vocabulary") == false)
                {
                    using (SQLiteCommand cmd = dbConn.CreateCommand())
                    {
                        cmd.CommandText = "CREATE TABLE tbl_vocabulary (idx integer PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, vocabulary varchar(50), vocabulary_gana varchar(50), vocabulary_translation TEXT, memorize_completed integer DEFAULT (0), memorize_target integer DEFAULT (1), registration_date integer);";
                        cmd.ExecuteNonQuery();

                        cmd.CommandText = "CREATE UNIQUE INDEX tbl_vocabulary_Index01 ON tbl_vocabulary(idx);";
                        cmd.ExecuteNonQuery();
                    }
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
            if (dbConn != null)
                dbConn.Close();

            dbConn = null;

            return true;
        }

        private void FillData()
        {
            FillVocabularyData(string.Empty);
            FillHanjaData(string.Empty);
        }

        private void FillVocabularyData(string sqlWhere)
        {
            Debug.Assert(dbConn != null);

            // 전체 행을 삭제합니다.
            dataWordGridView.Rows.Clear();
            
            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = "SELECT * FROM tbl_vocabulary";
                if (string.IsNullOrEmpty(sqlWhere) == false)
                    strSQL += " WHERE " + sqlWhere;

                SQLiteCommand cmd = new SQLiteCommand(strSQL, dbConn);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    while (reader.HasRows == true && reader.Read() == true)
                        dataWordGridView.Rows.Add(reader.GetInt32(0), reader.GetString(1), reader.GetString(2), reader.GetString(3));
                }
            }
            catch (SQLiteException e)
            {
            }
        }

        private void FillHanjaData(string sqlWhere)
        {
            Debug.Assert(dbConn != null);

            // 전체 행을 삭제합니다.
            dataHanjaGridView.Rows.Clear();

            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = "SELECT * FROM tbl_hanja";
                if (string.IsNullOrEmpty(sqlWhere) == false)
                    strSQL += " WHERE " + sqlWhere;

                SQLiteCommand cmd = new SQLiteCommand(strSQL, dbConn);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    while (reader.HasRows == true && reader.Read() == true)
                        dataHanjaGridView.Rows.Add(reader.GetInt32(0), reader.GetString(1), reader.GetString(2), reader.GetString(3), reader.GetString(4));
                }
            }
            catch (SQLiteException e)
            {
            }
        }

        private void dataWordGridView_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.ColumnIndex == 4)
            {
                if (dataWordGridView.Rows[e.RowIndex].Cells[4].Value == null || Boolean.Parse(dataWordGridView.Rows[e.RowIndex].Cells[4].Value.ToString()) == false)
                    dataWordGridView.Rows[e.RowIndex].Cells[4].Value = true;
                else
                    dataWordGridView.Rows[e.RowIndex].Cells[4].Value = false;
            }
        }

        private void btnExportXml_Click(object sender, EventArgs e)
        {
            SaveFileDialog dlg = new SaveFileDialog();
            dlg.Filter = "XML File|*.xml";
            dlg.Title = "저장하시려는 XML 파일명을 입력하여 주세요.";
            if (dlg.ShowDialog() != DialogResult.OK)
                return;

            bool bIsSaveAll = false;
            if (MessageBox.Show("전체 데이터를 XML 파일로 저장하시겠습니까?", "저장", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                bIsSaveAll = true;

            XmlTextWriter xmlTextWriter = new XmlTextWriter(dlg.FileName, Encoding.UTF8);
 
            // XML 문서를 생성할 때 자식 요소에 따라서 들여쓰기를 한다.
            xmlTextWriter.Formatting = Formatting.Indented;
 
            // XML선언을 작성한다.
            xmlTextWriter.WriteStartDocument();
 
            xmlTextWriter.WriteStartElement("vocabularys");
            foreach (DataGridViewRow row in dataWordGridView.Rows)
            {
                if (bIsSaveAll == true || (row.Cells[4].Value != null && Boolean.Parse(row.Cells[4].Value.ToString()) == true))
                {
                    xmlTextWriter.WriteStartElement("vocabulary");
                    xmlTextWriter.WriteElementString("hanja", row.Cells[1].Value.ToString());
                    xmlTextWriter.WriteElementString("speech", row.Cells[2].Value.ToString());

                    string description = row.Cells[3].Value.ToString();
                    string text = row.Cells[1].Value.ToString().Trim();
                    foreach (char c in text)
                    {
                        try
                        {
                            // 데이터를 읽어들입니다.
                            string strSQL = string.Format(@"SELECT * FROM tbl_hanja WHERE Word = ""{0}""", c);
                            SQLiteCommand cmd = new SQLiteCommand(strSQL, dbConn);
                            cmd.CommandType = CommandType.Text;

                            using (SQLiteDataReader reader = cmd.ExecuteReader())
                            {
                                if (reader.HasRows == true && reader.Read())
                                    description = string.Format("{0}\r\n\r\n{1}\r\n음독 : {3}\r\n훈독 : {4}\r\n{2}", description, reader.GetString(1), reader.GetString(4), reader.GetString(2), reader.GetString(3));
                            }
                        }
                        catch (SQLiteException ex)
                        {
                        }
                    }

                    xmlTextWriter.WriteElementString("description", description);
                    xmlTextWriter.WriteElementString("visible", "true");

                    xmlTextWriter.WriteEndElement();
                }
            }
            xmlTextWriter.WriteEndElement();
 
            xmlTextWriter.Flush();
            xmlTextWriter.Close();
        }

        #endregion
    }
}
