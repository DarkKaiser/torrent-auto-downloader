using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Data.SQLite;
using System.Net;
using System.IO;
using HtmlAgilityPack;

namespace JapanVocabularyDbManager
{
    public partial class frmHanja : Form
    {
        public bool EditMode { private get; set; }
        public long idx { get; set; }
        public string Character { get; set; }
        public string SoundRead { get; set; }
        public string MeanRead { get; set; }
        public string Translation { get; set; }
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

            EnableControls();
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            Close();
        }

        private void btnOk_Click(object sender, EventArgs e)
        {
            string strCharacter = txtCharacter.Text.Trim();
            string strSoundRead = txtSoundRead.Text.Trim();
            string strMeanRead = txtMeanRead.Text.Trim();
            string strTranslation = txtTranslation.Text.Trim();

            // 변경된 것이 없는지 확인한다.
            if (Character == strCharacter && SoundRead == strSoundRead && MeanRead == strMeanRead && Translation == strTranslation)
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
                    string strSQL = string.Format(@"SELECT * FROM TBL_HANJA WHERE IDX <> {0} AND CHARACTER = ""{1}""", idx, strCharacter);
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
                        updateCmd.CommandText = string.Format("UPDATE TBL_HANJA SET CHARACTER=?, SOUND_READ=?, MEAN_READ=?, TRANSLATION=? WHERE IDX={0};", idx);
                        SQLiteParameter param1 = new SQLiteParameter();
                        SQLiteParameter param2 = new SQLiteParameter();
                        SQLiteParameter param3 = new SQLiteParameter();
                        SQLiteParameter param4 = new SQLiteParameter();
                        updateCmd.Parameters.Add(param1);
                        updateCmd.Parameters.Add(param2);
                        updateCmd.Parameters.Add(param3);
                        updateCmd.Parameters.Add(param4);

                        param1.Value = strCharacter;
                        param2.Value = strSoundRead;
                        param3.Value = strMeanRead;
                        param4.Value = strTranslation;

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
                    string strSQL = string.Format(@"SELECT * FROM TBL_HANJA WHERE CHARACTER = ""{0}""", strCharacter);
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
                    cmd.CommandText = "INSERT INTO TBL_HANJA (CHARACTER, SOUND_READ, MEAN_READ, TRANSLATION) VALUES (?,?,?,?);";
                    SQLiteParameter param1 = new SQLiteParameter();
                    SQLiteParameter param2 = new SQLiteParameter();
                    SQLiteParameter param3 = new SQLiteParameter();
                    SQLiteParameter param4 = new SQLiteParameter();
                    cmd.Parameters.Add(param1);
                    cmd.Parameters.Add(param2);
                    cmd.Parameters.Add(param3);
                    cmd.Parameters.Add(param4);

                    param1.Value = strCharacter;
                    param2.Value = strSoundRead;
                    param3.Value = strMeanRead;
                    param4.Value = strTranslation;
                    cmd.ExecuteNonQuery();
                }
            }

            Character = strCharacter;
            SoundRead = strSoundRead;
            MeanRead = strMeanRead;
            Translation = strTranslation;

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

            if (EditMode == false)
            {
                string strClipboardText = Clipboard.GetText();
                if (strClipboardText.Length == 1)
                    txtCharacter.Text = strClipboardText;
            }
        }

        #endregion

        private void webBrowser_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            // 웹 브라우저 로딩이 완료될 때까지 대기한다.
            if (EditMode == false && e.Url.AbsoluteUri == webBrowser.Url.AbsoluteUri && webBrowser.Url.AbsoluteUri != "about:blank")
            {
                bool parseCompleted = false;

                // HTML 데이터를 파싱한다.
                HtmlAgilityPack.HtmlDocument htmlDoc = new HtmlAgilityPack.HtmlDocument();
                htmlDoc.LoadHtml(webBrowser.DocumentText);

                HtmlAgilityPack.HtmlNode bodyNode = htmlDoc.DocumentNode.SelectSingleNode("//body");
                HtmlAgilityPack.HtmlNodeCollection srchBoxNodeList = bodyNode.SelectNodes("//div[@class='srch_box']");
                foreach (HtmlAgilityPack.HtmlNode srchBoxNode in srchBoxNodeList)
                {
                    var characterNodeList = srchBoxNode.SelectSingleNode("//div[@class='srch_top']//a//span[@class='jp']");
                    if (characterNodeList != null && characterNodeList.InnerText == txtCharacter.Text.Trim())
                    {
                        String strMeadRead = "";
                        String strSoundRead = "";
                        String strTranslation = "";

                        // 음독, 훈독이 있는 노드
                        var htmlNodes = srchBoxNode.SelectNodes("./dl[@class='top_dn']/*");
                        if (htmlNodes != null)
                        {
                            for (int index = 0; index < htmlNodes.Count; ++index)
                            {
                                HtmlAgilityPack.HtmlNode htmlNode = htmlNodes.ElementAt(index);
                                if (htmlNode.InnerText.Equals("음독"))
                                {
                                    HtmlAgilityPack.HtmlNode htmlNextNode = htmlNodes.ElementAt(index + 1);
                                    if (htmlNextNode != null)
                                    {
                                        string text = htmlNextNode.InnerText;
                                        int pos = text.IndexOf('|');
                                        if (pos != -1)
                                            text = text.Substring(0, pos - 1);

                                        strSoundRead = text.Trim();
                                    }
                                }
                                else if (htmlNode.InnerText.Equals("훈독"))
                                {
                                    HtmlAgilityPack.HtmlNode htmlNextNode = htmlNodes.ElementAt(index + 1);
                                    if (htmlNextNode != null)
                                    {
                                        string text = htmlNextNode.InnerText;
                                        int pos = text.IndexOf('|');
                                        if (pos != -1)
                                            text = text.Substring(0, pos - 1);

                                        strMeadRead = text.Trim();
                                    }
                                }
                            }

                            // 뜻이 있는 노드
                            var translationNode = srchBoxNode.SelectSingleNode("//dl[@class='top_dn top_dn_v2']/dd[@class='ft_col3']/span[@class='ft_col3']");
                            if (translationNode != null)
                                strTranslation = translationNode.InnerText;
                        }

                        if (strSoundRead.Length == 0 || strMeadRead.Length == 0 || strTranslation.Length == 0)
                            break;

                        // 컨트롤에 값을 할당한다.
                        parseCompleted = true;
                        txtMeanRead.Text = strMeadRead;
                        txtSoundRead.Text = strSoundRead;
                        txtTranslation.Text = strTranslation;

                        break;
                    }
                }

                if (parseCompleted == false)
                    MessageBox.Show("파싱이 실패하였습니다.");
            }
        }
    }
}
