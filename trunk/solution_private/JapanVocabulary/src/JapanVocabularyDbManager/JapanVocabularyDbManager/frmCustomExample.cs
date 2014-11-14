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

            // 이미 입력된 예문인지 확인한다.
            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = string.Format(@"SELECT * FROM TBL_VOCABULARY_EXAMPLE WHERE V_IDX = {0} AND VOCABULARY = ""{1}""", idx, strExample);
                SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    if (reader.HasRows == true)
                    {
                        MessageBox.Show("DB에 이미 등록된 예문이 포함되어 있습니다. 이 항목은 추가에서 제외됩니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        return;
                    }
                }
            }
            catch (SQLiteException ex)
            {
                MessageBox.Show(string.Format("데이터 확인중에 오류가 발생하였습니다.\r\n\r\n{0}", ex.Message), "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            // 예문을 추가한다.
            using (SQLiteCommand cmd = DbConnection.CreateCommand())
            {
                cmd.CommandText = "INSERT INTO TBL_VOCABULARY_EXAMPLE (V_IDX, VOCABULARY, VOCABULARY_TRANSLATION) VALUES (?,?,?);";
                SQLiteParameter param1 = new SQLiteParameter();
                SQLiteParameter param2 = new SQLiteParameter();
                SQLiteParameter param3 = new SQLiteParameter();
                cmd.Parameters.Add(param1);
                cmd.Parameters.Add(param2);
                cmd.Parameters.Add(param3);

                param1.Value = idx;
                param2.Value = strExample;
                param3.Value = strExampleTranslation;
                cmd.ExecuteNonQuery();
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
    }
}
