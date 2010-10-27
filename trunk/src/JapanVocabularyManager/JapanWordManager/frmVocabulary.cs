using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Data.SQLite;

namespace JapanWordManager
{
    public partial class frmVocabulary : Form
    {
        public bool EditMode { private get; set; }

        public long idx { get; set; }
        public string Vocabulary { get; set; }
        public string VocabularyGana { get; set; }
        public string VocabularyTranslation { get; set; }
        public SQLiteConnection DbConnection { private get; set; }

        public frmVocabulary()
        {
            InitializeComponent();
        }

        private void EnableControls()
        {
            if (string.IsNullOrEmpty(txtVocabulary.Text.Trim()) == true || string.IsNullOrEmpty(txtVocabularyGana.Text.Trim()) == true || cboPartsOfSpeech.SelectedIndex == -1)
                btnOk.Enabled = false;
            else
                btnOk.Enabled = true;
        }

        private void CheckVocabularyExtensionInfo()
        {
            txtExtensionInfo.Text = string.Empty;

            string text = txtVocabulary.Text.Trim();
            foreach (char c in text)
            {
                try
                {
                    // 데이터를 읽어들입니다.
                    string strSQL = string.Format(@"SELECT IDX, CHARACTER, SOUND_READ, MEAN_READ, JLPT_CLASS, TRANSLATION FROM TBL_HANJA WHERE CHARACTER = ""{0}""", c);
                    SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                    cmd.CommandType = CommandType.Text;

                    using (SQLiteDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.HasRows == true && reader.Read())
                            txtExtensionInfo.Text += string.Format("{0}\r\n음독 : {2}\r\n훈독 : {3}\r\n{1}\r\n\r\n", reader.GetString(1/*CHARACTER*/), reader.GetString(5/*TRANSLATION*/), reader.GetString(2/*SOUND_READ*/), reader.GetString(3/*MEAN_READ*/));
                    }
                }
                catch (SQLiteException ex)
                {
                }
            }
        }

        // @@@@@
        private void frmVocabulary_Load(object sender, EventArgs e)
        {
            txtVocabulary.Text = Vocabulary;
            txtVocabularyGana.Text = VocabularyGana;
            txtVocabularyTranslation.Text = VocabularyTranslation;

            EnableControls();
            CheckVocabularyExtensionInfo();
        }

        private void frmVocabulary_Shown(object sender, EventArgs e)
        {
            txtVocabulary.Focus();
        }

        // @@@@@
        private void btnOk_Click(object sender, EventArgs e)
        {
            string vocabulary = txtVocabulary.Text.Trim();
            string higaVocabulary = txtVocabularyGana.Text.Trim();
            string description = txtVocabularyTranslation.Text.Trim();

            // 변경된 것이 없는지 확인한다.
            if (Vocabulary == vocabulary && VocabularyGana == higaVocabulary && VocabularyTranslation == description)
            {
                DialogResult = DialogResult.Cancel;
                Close();
                return;
            }

            if (EditMode == true)
            {
                // 이미 입력된 단어인지 확인한다.
                try
                {
                    // 데이터를 읽어들입니다.
                    string strSQL = string.Format(@"SELECT * FROM tbl_vocabulary WHERE idx <> {0} AND vocabulary = ""{1}""", idx, vocabulary);
                    SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                    cmd.CommandType = CommandType.Text;

                    using (SQLiteDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.HasRows == true)
                        {
                            MessageBox.Show("DB에 이미 등록된 단어입니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            txtVocabulary.Focus();
                            return;
                        }
                    }

                    // 데이터를 갱신한다.
                    using (SQLiteCommand updateCmd = DbConnection.CreateCommand())
                    {
                        updateCmd.CommandText = string.Format("UPDATE tbl_vocabulary SET vocabulary=?, vocabulary_gana=?, vocabulary_translation=?, registration_date=? WHERE idx={0};", idx);
                        SQLiteParameter param1 = new SQLiteParameter();
                        SQLiteParameter param2 = new SQLiteParameter();
                        SQLiteParameter param3 = new SQLiteParameter();
                        SQLiteParameter param4 = new SQLiteParameter();
                        updateCmd.Parameters.Add(param1);
                        updateCmd.Parameters.Add(param2);
                        updateCmd.Parameters.Add(param3);
                        updateCmd.Parameters.Add(param4);

                        param1.Value = vocabulary;
                        param2.Value = higaVocabulary;
                        param3.Value = description;
                        param4.Value = (DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalMilliseconds;

                        updateCmd.ExecuteNonQuery();
                    }
                }
                catch (SQLiteException ex)
                {
                    MessageBox.Show(string.Format("데이터 확인중에 오류가 발생하였습니다.\r\n\r\n{0}", ex.Message), "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
            }
            else
            {
                // 이미 입력된 단어인지 확인한다.
                try
                {
                    // 데이터를 읽어들입니다.
                    string strSQL = string.Format(@"SELECT * FROM tbl_vocabulary WHERE vocabulary = ""{0}""", vocabulary);
                    SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                    cmd.CommandType = CommandType.Text;

                    using (SQLiteDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.HasRows == true)
                        {
                            if (MessageBox.Show("DB에 이미 등록된 단어입니다.\nDB의 데이터를 업데이트 하시겠습니까?", "오류", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.Cancel)
                            {
                                txtVocabulary.Focus();
                                return;
                            }

                            // 데이터를 갱신한다.
                            using (SQLiteCommand updateCmd = DbConnection.CreateCommand())
                            {
                                reader.Read();
                                updateCmd.CommandText = string.Format("UPDATE tbl_vocabulary SET vocabulary=?, vocabulary_gana=?, vocabulary_translation=?, registration_date=? WHERE idx={0};", reader.GetInt32(0));
                                SQLiteParameter param1 = new SQLiteParameter();
                                SQLiteParameter param2 = new SQLiteParameter();
                                SQLiteParameter param3 = new SQLiteParameter();
                                SQLiteParameter param4 = new SQLiteParameter();
                                updateCmd.Parameters.Add(param1);
                                updateCmd.Parameters.Add(param2);
                                updateCmd.Parameters.Add(param3);
                                updateCmd.Parameters.Add(param4);
                                param4.Value = (DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalMilliseconds;

                                param1.Value = vocabulary;
                                param2.Value = higaVocabulary;
                                param3.Value = description;

                                updateCmd.ExecuteNonQuery();
                            }

                            Vocabulary = vocabulary;
                            VocabularyGana = higaVocabulary;
                            VocabularyTranslation = description;

                            DialogResult = DialogResult.OK;
                            Close();

                            return;
                        }
                    }
                }
                catch (SQLiteException ex)
                {
                    MessageBox.Show(string.Format("데이터 확인중에 오류가 발생하였습니다.\r\n\r\n{0}", ex.Message), "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                // 데이터를 추가한다.
                using (SQLiteCommand cmd = DbConnection.CreateCommand())
                {
                    cmd.CommandText = "INSERT INTO tbl_vocabulary (vocabulary, vocabulary_gana, vocabulary_translation, registration_date) VALUES (?,?,?,?);";
                    SQLiteParameter param1 = new SQLiteParameter();
                    SQLiteParameter param2 = new SQLiteParameter();
                    SQLiteParameter param3 = new SQLiteParameter();
                    SQLiteParameter param4 = new SQLiteParameter();
                    cmd.Parameters.Add(param1);
                    cmd.Parameters.Add(param2);
                    cmd.Parameters.Add(param3);
                    cmd.Parameters.Add(param4);

                    param1.Value = vocabulary;
                    param2.Value = higaVocabulary;
                    param3.Value = description;
                    param4.Value = (DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalMilliseconds;
                    cmd.ExecuteNonQuery();
                }
            }

            Vocabulary = vocabulary;
            VocabularyGana = higaVocabulary;
            VocabularyTranslation = description;

            DialogResult = DialogResult.OK;
            Close();
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            Close();
        }

        private void btnAddHanja_Click(object sender, EventArgs e)
        {
            frmHanja form = new frmHanja();
            form.DbConnection = DbConnection;
            form.EditMode = false;

            if (form.ShowDialog() == DialogResult.OK)
                CheckVocabularyExtensionInfo();
        }

        private void txtVocabulary_TextChanged(object sender, EventArgs e)
        {
            EnableControls();
            CheckVocabularyExtensionInfo();
        }

        private void txtHiGaVocabulary_TextChanged(object sender, EventArgs e)
        {
            EnableControls();
        }

        private void txtVocabulary_Leave(object sender, EventArgs e)
        {
            txtVocabulary.Text = txtVocabulary.Text.Trim();
        }

        private void txtHiGaVocabulary_Leave(object sender, EventArgs e)
        {
            txtVocabularyGana.Text = txtVocabularyGana.Text.Trim();
        }

        private void txtDescription_Leave(object sender, EventArgs e)
        {
            txtVocabularyTranslation.Text = txtVocabularyTranslation.Text.Trim();
        }
    }
}
