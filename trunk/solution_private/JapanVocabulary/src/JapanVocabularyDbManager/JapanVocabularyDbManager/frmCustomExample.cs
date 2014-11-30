using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Data.SQLite;

namespace JapanVocabularyDbManager
{
    public partial class frmCustomExample : Form
    {
        public long idx { get; set; }
        public String Vocabulary { get; set; }
        public String VocabularyGana { get; set; }
        public SQLiteConnection DbConnection { private get; set; }

        public frmCustomExample()
        {
            InitializeComponent();
        }

        private void btnAdd_Click(object sender, EventArgs e)
        {
            string strExample = txtExample.Text.Trim();
            string strExampleTranslation = txtExampleTranslation.Text.Trim();
            
            if (strExample.Length == 0 || strExampleTranslation.Length == 0)
            {
                MessageBox.Show("예문이나 뜻이 입력되지 않았습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            if (strExample.IndexOf(Vocabulary) == -1 && strExample.IndexOf(VocabularyGana) == -1)
            {
                MessageBox.Show("입력한 예문에 단어 혹은 히라가나/가타가나가 포함되어 있지 않습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            // 이미 입력된 예문인지 확인한다.
            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = string.Format(@"SELECT * FROM TBL_VOCABULARY_EXAMPLE WHERE VOCABULARY = ""{0}""", strExample);
                SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    if (reader.HasRows == true)
                    {
                        MessageBox.Show("DB에 이미 동일한 예문이 등록되어 있습니다.\n이 예문은 추가할 수 없습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        return;
                    }
                }
            }
            catch (SQLiteException ex)
            {
                MessageBox.Show(string.Format("기존에 등록된 예문인지 확인중에 오류가 발생하였습니다.\r\n\r\n{0}", ex.Message), "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            
            // 예문을 추가한다.
            using (SQLiteTransaction tran = DbConnection.BeginTransaction())
            {
                using (SQLiteCommand cmd = DbConnection.CreateCommand())
                {
                    cmd.CommandText = "INSERT INTO TBL_VOCABULARY_EXAMPLE (VOCABULARY, VOCABULARY_TRANSLATION) VALUES (?,?);";
                    SQLiteParameter param1 = new SQLiteParameter();
                    SQLiteParameter param2 = new SQLiteParameter();
                    cmd.Parameters.Add(param1);
                    cmd.Parameters.Add(param2);

                    param1.Value = strExample;
                    param2.Value = strExampleTranslation;
                    cmd.ExecuteNonQuery();
                }

                using (SQLiteCommand cmd = DbConnection.CreateCommand())
                {
                    cmd.CommandText = "INSERT INTO TBL_VOCABULARY_EXAMPLE_MAPP (V_IDX, E_IDX) VALUES (?, (select last_insert_rowid()) );";
                    SQLiteParameter param1 = new SQLiteParameter();
                    cmd.Parameters.Add(param1);

                    param1.Value = idx;
                    cmd.ExecuteNonQuery();
                }

                tran.Commit();
            }

            DialogResult = DialogResult.OK;
            Close();
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            Close();
        }

        private void btnAddTag_Click(object sender, EventArgs e)
        {
            int nSelectionStart = txtExample.SelectionStart;
            txtExample.Text = txtExample.Text.Insert(nSelectionStart, "<sup><font color='#f67474'></font></sup>");
        }

        private void txtExample_TextChanged(object sender, EventArgs e)
        {
            webBrowser.DocumentText = txtExample.Text;
        }

        private void frmCustomExample_Shown(object sender, EventArgs e)
        {
            txtExample.Focus();
        }
    }
}
