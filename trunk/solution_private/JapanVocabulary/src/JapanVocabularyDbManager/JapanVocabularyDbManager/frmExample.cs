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
    public partial class frmExample : Form
    {
        public long idx { get; set; }
        public SQLiteConnection DbConnection { private get; set; }
        public List<ExampleInfo> ExampleInfoList { private get; set; }

        public frmExample()
        {
            InitializeComponent();
        }

        private void frmExample_Load(object sender, EventArgs e)
        {
            // 그리드에 추가한다.
            foreach (ExampleInfo ex in ExampleInfoList)
                dataExampleGridView.Rows.Add(false, ex.example, ex.example_translation);
        }

        private void btnOK_Click(object sender, EventArgs e)
        {
            bool hasAddExample = false;
            for (int index = 0; index < dataExampleGridView.Rows.Count; ++index)
            {
                if (Boolean.Parse(dataExampleGridView.Rows[index].Cells[0].FormattedValue.ToString()) == true)
                {
                    string example = dataExampleGridView.Rows[index].Cells[1].FormattedValue.ToString().Trim();
                    string translation = dataExampleGridView.Rows[index].Cells[2].FormattedValue.ToString().Trim();
                    if (example.Length == 0 || translation.Length == 0)
                    {
                        MessageBox.Show("예문 혹은 뜻이 비어있는 항목이 있습니다. 이 항목은 추가에서 제외됩니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        continue;
                    }

                    // 이미 입력된 예문인지 확인한다.
                    try
                    {
                        // 데이터를 읽어들입니다.
                        string strSQL = string.Format(@"SELECT * FROM TBL_VOCABULARY_EXAMPLE WHERE VOCABULARY = ""{0}""", example);
                        SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                        cmd.CommandType = CommandType.Text;

                        using (SQLiteDataReader reader = cmd.ExecuteReader())
                        {
                            if (reader.HasRows == true)
                            {
                                MessageBox.Show("DB에 이미 등록된 예문이 포함되어 있습니다.\n이 항목은 추가에서 제외됩니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                                continue;
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

                            param1.Value = example;
                            param2.Value = translation;
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

                    hasAddExample = true;
                }
            }

            if (hasAddExample == false)
            {
                MessageBox.Show("선택된 예문이 없습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            DialogResult = DialogResult.OK;
            Close();
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            Close();
        }

        private void dataExampleGridView_RowEnter(object sender, DataGridViewCellEventArgs e)
        {
            if (dataExampleGridView.SelectedRows.Count == 0)
                return;

            webBrowser.DocumentText = dataExampleGridView.SelectedRows[0].Cells[1].FormattedValue.ToString();
        }
    }
}
