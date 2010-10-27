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
    public partial class frmHanja : Form
    {
        public bool EditMode { private get; set; }

        public long idx { get; set; }
        public string Character { get; set; }
        public string SoundRead { get; set; }
        public string MeanRead { get; set; }
        public string Translation { get; set; }
        public int JLPTClass { get; set; }
        public SQLiteConnection DbConnection { private get; set; }

        public frmHanja()
        {
            InitializeComponent();
        }

        private void EnableControls()
        {
            if (string.IsNullOrEmpty(txtCharacter.Text.Trim()) == true ||
                (string.IsNullOrEmpty(txtSoundRead.Text.Trim()) == true && string.IsNullOrEmpty(txtMeanRead.Text.Trim()) == true))
            {
                btnOk.Enabled = false;
            } else {
                btnOk.Enabled = true;
            }
        }

        #region 이벤트 핸들러

        private void frmHanja_Load(object sender, EventArgs e)
        {
            txtCharacter.Text = Character;
            txtSoundRead.Text = SoundRead;
            txtMeanRead.Text = MeanRead;
            txtTranslation.Text = Translation;

            if (JLPTClass == 99)
            {
                cboJlptLevel.SelectedIndex = 5;
            }
            else
            {
                cboJlptLevel.SelectedIndex = JLPTClass - 1;
            }

            EnableControls();
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            Close();
        }

        // @@@@@
        private void btnOk_Click(object sender, EventArgs e)
        {
            string hanja = txtCharacter.Text.Trim();
            string ymdok = txtSoundRead.Text.Trim();
            string hundok = txtMeanRead.Text.Trim();
            string description = txtTranslation.Text.Trim();

            // 변경된 것이 없는지 확인한다.
            if (Character == hanja && SoundRead == ymdok && MeanRead == hundok && Translation == description)
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
                    string strSQL = string.Format(@"SELECT * FROM tbl_hanja WHERE idx <> {0} AND Word = ""{1}""", idx, hanja);
                    SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                    cmd.CommandType = CommandType.Text;

                    using (SQLiteDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.HasRows == true)
                        {
                            MessageBox.Show("DB에 이미 등록된 단어입니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            txtCharacter.Focus();
                            return;
                        }
                    }

                    // 데이터를 갱신한다.
                    using (SQLiteCommand updateCmd = DbConnection.CreateCommand())
                    {
                        updateCmd.CommandText = string.Format("UPDATE tbl_hanja SET Word=?, YmDok=?, HunDok=?, Description=? WHERE idx={0};", idx);
                        SQLiteParameter param1 = new SQLiteParameter();
                        SQLiteParameter param2 = new SQLiteParameter();
                        SQLiteParameter param3 = new SQLiteParameter();
                        SQLiteParameter param4 = new SQLiteParameter();
                        updateCmd.Parameters.Add(param1);
                        updateCmd.Parameters.Add(param2);
                        updateCmd.Parameters.Add(param3);
                        updateCmd.Parameters.Add(param4);

                        param1.Value = hanja;
                        param2.Value = ymdok;
                        param3.Value = hundok;
                        param4.Value = description;

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
                    string strSQL = string.Format(@"SELECT * FROM tbl_hanja WHERE Word = ""{0}""", hanja);
                    SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                    cmd.CommandType = CommandType.Text;

                    using (SQLiteDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.HasRows == true)
                        {
                            MessageBox.Show("DB에 이미 등록된 단어입니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            txtCharacter.Focus();
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
                    cmd.CommandText = "INSERT INTO tbl_hanja (Word, YmDok, HunDok, Description) VALUES (?,?,?,?);";
                    SQLiteParameter param1 = new SQLiteParameter();
                    SQLiteParameter param2 = new SQLiteParameter();
                    SQLiteParameter param3 = new SQLiteParameter();
                    SQLiteParameter param4 = new SQLiteParameter();
                    cmd.Parameters.Add(param1);
                    cmd.Parameters.Add(param2);
                    cmd.Parameters.Add(param3);
                    cmd.Parameters.Add(param4);

                    param1.Value = hanja;
                    param2.Value = ymdok;
                    param3.Value = hundok;
                    param4.Value = description;
                    cmd.ExecuteNonQuery();
                }
            }

            Character = hanja;
            SoundRead = ymdok;
            MeanRead = hundok;
            Translation = description;

            DialogResult = DialogResult.OK;
            Close();
        }

        private void txtHanja_TextChanged(object sender, EventArgs e)
        {
            EnableControls();

            if (string.IsNullOrEmpty(txtCharacter.Text.Trim()) == false)
                webBrowser.Url = new Uri(string.Format("http://jpdic.naver.com/search.nhn?query={0}", txtCharacter.Text.Trim()));
        }

        private void txtYmDok_TextChanged(object sender, EventArgs e)
        {
            EnableControls();
        }

        private void txtHunDok_TextChanged(object sender, EventArgs e)
        {
            EnableControls();
        }

        private void cboJlptLevel_SelectedIndexChanged(object sender, EventArgs e)
        {
            EnableControls();
        }

        private void txtDescription_TextChanged(object sender, EventArgs e)
        {
            EnableControls();
        }
        
        private void txtHanja_Leave(object sender, EventArgs e)
        {
            txtCharacter.Text = txtCharacter.Text.Trim();
        }

        private void txtYmDok_Leave(object sender, EventArgs e)
        {
            txtSoundRead.Text = txtSoundRead.Text.Trim();
        }

        private void txtHunDok_Leave(object sender, EventArgs e)
        {
            txtMeanRead.Text = txtMeanRead.Text.Trim();
        }

        private void txtDescription_Leave(object sender, EventArgs e)
        {
            txtTranslation.Text = txtTranslation.Text.Trim();
        }

        private void frmHanja_Shown(object sender, EventArgs e)
        {
            txtCharacter.Focus();

            string strClipboardText = Clipboard.GetText();
            if (strClipboardText.Length == 1)
                txtCharacter.Text = strClipboardText;
        }

        #endregion

        private void webBrowser_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            // 웹 브라우저가 로딩이 완료될 때까지 대기한다.
            if (EditMode == false && e.Url.AbsoluteUri == webBrowser.Url.AbsoluteUri && webBrowser.Url.AbsoluteUri != "about:blank")
            {
                string strDocumentText = webBrowser.DocumentText;

                int first = strDocumentText.IndexOf(@"class=""entry_result""");
                if (first == -1)
                    return;

                int last = strDocumentText.IndexOf("</dl>", first);
                if (last == -1)
                    return;

                string strContent = strDocumentText.Substring(first, last - first);

                first = strContent.IndexOf(txtCharacter.Text.Trim());
                if (first == -1)
                    return;

                first = strContent.IndexOf("<dd class=", first);
                if (first == -1)
                    return;

                last = strContent.IndexOf("<dt>", first);
                if (last == -1)
                    return;

                int jlptClass = 5;
                int levelPos = strContent.IndexOf("ico_jlpt");
                if (levelPos != -1)
                {
                    string strJLPTLevel = strContent.Substring(levelPos + 8, 1);
                    jlptClass = int.Parse(strJLPTLevel) - 1;
                }

                strContent = strContent.Substring(first, last - first);

                if (string.IsNullOrEmpty(strContent) == false)
                {
                    string temp;

                    // 음독을 찾는다.
                    first = strContent.IndexOf(@"""음독""");
                    if (first != -1)
                    {
                        last = strContent.IndexOf("\r\n", first);
                        temp = strContent.Substring(first, last - first);
                        if (string.IsNullOrEmpty(temp) == false)
                        {
                            first = temp.IndexOf(@"class=""jp"">");
                            if (first != -1)
                            {
                                first += 11;
                                last = temp.IndexOf("</span>", first);
                                if (last != -1)
                                    txtSoundRead.Text = temp.Substring(first, last - first);
                            }
                        }
                    }

                    // 훈독을 찾는다.
                    first = strContent.IndexOf(@"""훈독""");
                    if (first != -1)
                    {
                        last = strContent.IndexOf("\r\n", first);
                        temp = strContent.Substring(first, last - first);
                        if (string.IsNullOrEmpty(temp) == false)
                        {
                            first = temp.IndexOf(@"class=""jp"">");
                            if (first != -1)
                            {
                                first += 11;
                                last = temp.IndexOf("</span>", first);
                                if (last != -1)
                                    txtMeanRead.Text = temp.Substring(first, last - first);
                            }
                        }
                    }

                    // 뜻을 찾는다.
                    first = strContent.IndexOf(@"<dd class=""stroke"">");
                    if (first != -1)
                    {
                        first += 19;
                        last = strContent.IndexOf("<em>", first);
                        if (last != -1)
                            txtTranslation.Text = strContent.Substring(first, last - first);
                    }

                    // JLPT 클래스를 지정한다.
                    cboJlptLevel.SelectedIndex = jlptClass;
                }
            }
        }
    }
}
