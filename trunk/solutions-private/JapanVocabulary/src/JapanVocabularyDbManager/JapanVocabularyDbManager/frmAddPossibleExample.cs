using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.SQLite;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace JapanVocabularyDbManager
{
    public partial class frmAddPossibleExample : Form
    {
        public long idx { get; set; }
        public SQLiteConnection DbConnection { private get; set; }
        public List<AddPossibleExampleInfo> ExampleInfoList { private get; set; }

        public frmAddPossibleExample()
        {
            InitializeComponent();
        }

        private void frmAddPossibleExample_Load(object sender, EventArgs e)
        {
            // 그리드에 추가한다.
            foreach (AddPossibleExampleInfo ex in ExampleInfoList)
                dataExampleGridView.Rows.Add(false, ex.idx, ex.example, ex.example_translation);
        }

        private void btnOK_Click(object sender, EventArgs e)
        {
            bool hasAddExample = false;
            for (int index = 0; index < dataExampleGridView.Rows.Count; ++index)
            {
                if (Boolean.Parse(dataExampleGridView.Rows[index].Cells[0].FormattedValue.ToString()) == true)
                {
                    long e_idx = long.Parse(dataExampleGridView.Rows[index].Cells[1].FormattedValue.ToString());
                    string example = dataExampleGridView.Rows[index].Cells[2].FormattedValue.ToString().Trim();
                    string translation = dataExampleGridView.Rows[index].Cells[3].FormattedValue.ToString().Trim();
                    if (example.Length == 0 || translation.Length == 0)
                    {
                        MessageBox.Show("예문 혹은 뜻이 비어있는 항목이 있습니다. 이 항목은 추가에서 제외됩니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        continue;
                    }

                    // 예문매핑정보를 추가한다.
                    using (SQLiteTransaction tran = DbConnection.BeginTransaction())
                    {
                        using (SQLiteCommand cmd = DbConnection.CreateCommand())
                        {
                            cmd.CommandText = "INSERT INTO TBL_VOCABULARY_EXAMPLE_MAPP (V_IDX, E_IDX) VALUES (?,?);";
                            SQLiteParameter param1 = new SQLiteParameter();
                            SQLiteParameter param2 = new SQLiteParameter();
                            cmd.Parameters.Add(param1);
                            cmd.Parameters.Add(param2);

                            param1.Value = idx;
                            param2.Value = e_idx;
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

        private void dataExampleGridView_SelectionChanged(object sender, EventArgs e)
        {
            if (dataExampleGridView.SelectedRows.Count == 0)
                return;

            webBrowser.DocumentText = dataExampleGridView.SelectedRows[0].Cells[2].FormattedValue.ToString();
        }
    }
}
