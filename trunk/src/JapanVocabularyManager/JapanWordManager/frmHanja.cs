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
        public string Word { get; set; }
        public string YmDok { get; set; }
        public string HunDok { get; set; }
        public string Description { get; set; }
        public SQLiteConnection DbConnection { private get; set; }

        public frmHanja()
        {
            InitializeComponent();
        }

        private void EnableControls()
        {
            if (string.IsNullOrEmpty(txtHanja.Text.Trim()) == true || string.IsNullOrEmpty(txtDescription.Text.Trim()) == true ||
                (string.IsNullOrEmpty(txtYmDok.Text.Trim()) && string.IsNullOrEmpty(txtHunDok.Text.Trim())))
            {
                btnOk.Enabled = false;
            }
            else
            {
                btnOk.Enabled = true;
            }
        }

        #region 이벤트 핸들러

        private void frmHanja_Load(object sender, EventArgs e)
        {
            txtHanja.Text = Word;
            txtYmDok.Text = YmDok;
            txtHunDok.Text = HunDok;
            txtDescription.Text = Description;

            EnableControls();
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            Close();
        }

        private void btnOk_Click(object sender, EventArgs e)
        {
            string hanja = txtHanja.Text.Trim();
            string ymdok = txtYmDok.Text.Trim();
            string hundok = txtHunDok.Text.Trim();
            string description = txtDescription.Text.Trim();

            // 변경된 것이 없는지 확인한다.
            if (Word == hanja && YmDok == ymdok && HunDok == hundok && Description == description)
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
                            txtHanja.Focus();
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
                            txtHanja.Focus();
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

            Word = hanja;
            YmDok = ymdok;
            HunDok = hundok;
            Description = description;

            DialogResult = DialogResult.OK;
            Close();
        }

        private void txtHanja_TextChanged(object sender, EventArgs e)
        {
            EnableControls();

            if (string.IsNullOrEmpty(txtHanja.Text.Trim()) == false)
                webBrowser.Url = new Uri(string.Format("http://jpdic.naver.com/search.nhn?query={0}", txtHanja.Text.Trim()));
        }

        private void txtYmDok_TextChanged(object sender, EventArgs e)
        {
            EnableControls();
        }

        private void txtHunDok_TextChanged(object sender, EventArgs e)
        {
            EnableControls();
        }

        private void txtDescription_TextChanged(object sender, EventArgs e)
        {
            EnableControls();
        }
        
        private void txtHanja_Leave(object sender, EventArgs e)
        {
            txtHanja.Text = txtHanja.Text.Trim();
        }

        private void txtYmDok_Leave(object sender, EventArgs e)
        {
            txtYmDok.Text = txtYmDok.Text.Trim();
        }

        private void txtHunDok_Leave(object sender, EventArgs e)
        {
            txtHunDok.Text = txtHunDok.Text.Trim();
        }

        private void txtDescription_Leave(object sender, EventArgs e)
        {
            txtDescription.Text = txtDescription.Text.Trim();
        }

        private void frmHanja_Shown(object sender, EventArgs e)
        {
            txtHanja.Focus();

            string strClipboardText = Clipboard.GetText();
            if (strClipboardText.Length == 1)
                txtHanja.Text = strClipboardText;
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

                first = strContent.IndexOf(txtHanja.Text.Trim());
                if (first == -1)
                    return;

                first = strContent.IndexOf("<dd class=", first);
                if (first == -1)
                    return;

                last = strContent.IndexOf("<dt>", first);
                if (last == -1)
                    return;

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
                                    txtYmDok.Text = temp.Substring(first, last - first);
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
                                    txtHunDok.Text = temp.Substring(first, last - first);
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
                            txtDescription.Text = strContent.Substring(first, last - first);
                    }
                }
            }
        }
    }
}
