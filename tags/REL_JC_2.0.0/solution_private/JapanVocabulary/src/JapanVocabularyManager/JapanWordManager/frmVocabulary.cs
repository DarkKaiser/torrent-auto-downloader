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
    public struct ExampleInfo
    {
        public string example;
        public string example_translation;
    };

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
            if (string.IsNullOrEmpty(txtVocabulary.Text.Trim()) == true || string.IsNullOrEmpty(txtVocabularyGana.Text.Trim()) == true)
            {
                btnOk.Enabled = false;
                btnAddNoClose.Enabled = false;
            }
            else
            {
                btnOk.Enabled = true;
                btnAddNoClose.Enabled = true;
            }
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

        private void frmVocabulary_Load(object sender, EventArgs e)
        {
            txtVocabulary.Text = Vocabulary;
            txtVocabularyGana.Text = VocabularyGana;
            txtVocabularyTranslation.Text = VocabularyTranslation;

            EnableControls();
            CheckVocabularyExtensionInfo();

            if (EditMode == true)
            {
                FillExampleData();
                btnExampleAdd.Visible = true;
                btnExampleCustomAdd.Visible = true;
                dataExampleGridView.Visible = true;
                exampleWebBrowser.Visible = true;
            }
            else
            {
                btnExampleAdd.Visible = false;
                btnExampleCustomAdd.Visible = false;
                dataExampleGridView.Visible = false;
                exampleWebBrowser.Visible = false;
            }

            if (txtVocabulary.Text.Length > 0)
                webBrowser1.Url = new Uri(string.Format("http://jpdic.naver.com/search.nhn?query={0}", txtVocabulary.Text.Trim()));
        }

        private void frmVocabulary_Shown(object sender, EventArgs e)
        {
            txtVocabulary.Focus();
        }

        private void btnOk_Click(object sender, EventArgs e)
        {
            if (addVocabulary() == false)
                return;

            DialogResult = DialogResult.OK;
            Close();
        }

        private void btnAddNoClose_Click(object sender, EventArgs e)
        {
            if (addVocabulary() == false)
                return;

            if (EditMode == false)
            {
                EditMode = true;

                // 방금 추가한 단어의 idx 값을 구한다.
                string strSQL = string.Format(@"SELECT IDX FROM TBL_VOCABULARY WHERE VOCABULARY = ""{0}""", txtVocabulary.Text.Trim());
                SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    if (reader.HasRows == false)
                    {
                        MessageBox.Show("방금 추가한 단어의 IDX 값을 구하지 못하였습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        DialogResult = DialogResult.OK;
                        Close();
                        return;
                    }

                    int nRowCount = 0;
                    while (reader.Read() == true)
                    {
                        ++nRowCount;
                        if (nRowCount >= 2)
                        {
                            MessageBox.Show("방금 추가한 단어의 IDX 값이 2개 이상 존재합니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            DialogResult = DialogResult.OK;
                            Close();
                            return;
                        }

                        idx = reader.GetInt32(0/*IDX*/);
                    }
                }

                FillExampleData();
                btnExampleAdd.Visible = true;
                btnExampleCustomAdd.Visible = true;
                dataExampleGridView.Visible = true;
                exampleWebBrowser.Visible = true;
            }
        }

        private bool addVocabulary()
        {
            string strVocabulary = txtVocabulary.Text.Trim();
            string strVocabularyGana = txtVocabularyGana.Text.Trim();
            string strVocabularyTranslation = txtVocabularyTranslation.Text.Trim();

            if (EditMode == true)
            {
                // 이미 입력된 단어인지 확인한다.
                try
                {
                    // 데이터를 읽어들입니다.
                    string strSQL = string.Format(@"SELECT * FROM TBL_VOCABULARY WHERE IDX <> {0} AND VOCABULARY = ""{1}""", idx, strVocabulary);
                    SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                    cmd.CommandType = CommandType.Text;

                    using (SQLiteDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.HasRows == true)
                        {
                            MessageBox.Show("DB에 이미 등록된 단어입니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            txtVocabulary.Focus();
                            return false;
                        }
                    }

                    // 데이터를 갱신한다.
                    using (SQLiteCommand updateCmd = DbConnection.CreateCommand())
                    {
                        updateCmd.CommandText = string.Format("UPDATE TBL_VOCABULARY SET VOCABULARY=?, VOCABULARY_GANA=?, VOCABULARY_TRANSLATION=?, REGISTRATION_DATE=? WHERE IDX={0};", idx);
                        SQLiteParameter param1 = new SQLiteParameter();
                        SQLiteParameter param2 = new SQLiteParameter();
                        SQLiteParameter param3 = new SQLiteParameter();
                        SQLiteParameter param4 = new SQLiteParameter();
                        updateCmd.Parameters.Add(param1);
                        updateCmd.Parameters.Add(param2);
                        updateCmd.Parameters.Add(param3);
                        updateCmd.Parameters.Add(param4);

                        param1.Value = strVocabulary;
                        param2.Value = strVocabularyGana;
                        param3.Value = strVocabularyTranslation;
                        param4.Value = (DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalMilliseconds;

                        updateCmd.ExecuteNonQuery();
                    }
                }
                catch (SQLiteException ex)
                {
                    MessageBox.Show(string.Format("데이터 확인중에 오류가 발생하였습니다.\r\n\r\n{0}", ex.Message), "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return false;
                }
            }
            else
            {
                // 이미 입력된 단어인지 확인한다.
                try
                {
                    // 데이터를 읽어들입니다.
                    string strSQL = string.Format(@"SELECT * FROM TBL_VOCABULARY WHERE VOCABULARY = ""{0}""", strVocabulary);
                    SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                    cmd.CommandType = CommandType.Text;

                    using (SQLiteDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.HasRows == true)
                        {
                            MessageBox.Show("DB에 이미 등록된 단어입니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            txtVocabulary.Focus();
                            return false;
                        }
                    }
                }
                catch (SQLiteException ex)
                {
                    MessageBox.Show(string.Format("데이터 확인중에 오류가 발생하였습니다.\r\n\r\n{0}", ex.Message), "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return false;
                }

                // 데이터를 추가한다.
                using (SQLiteCommand cmd = DbConnection.CreateCommand())
                {
                    cmd.CommandText = "INSERT INTO TBL_VOCABULARY (VOCABULARY, VOCABULARY_GANA, VOCABULARY_TRANSLATION, REGISTRATION_DATE) VALUES (?,?,?,?);";
                    SQLiteParameter param1 = new SQLiteParameter();
                    SQLiteParameter param2 = new SQLiteParameter();
                    SQLiteParameter param3 = new SQLiteParameter();
                    SQLiteParameter param4 = new SQLiteParameter();
                    cmd.Parameters.Add(param1);
                    cmd.Parameters.Add(param2);
                    cmd.Parameters.Add(param3);
                    cmd.Parameters.Add(param4);

                    param1.Value = strVocabulary;
                    param2.Value = strVocabularyGana;
                    param3.Value = strVocabularyTranslation;
                    param4.Value = (DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalMilliseconds;
                    cmd.ExecuteNonQuery();
                }
            }

            Vocabulary = strVocabulary;
            VocabularyGana = strVocabularyGana;
            VocabularyTranslation = strVocabularyTranslation;

            return true;
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

        private void dataExampleGridView_UserDeletingRow(object sender, DataGridViewRowCancelEventArgs e)
        {
            if (MessageBox.Show("선택하신 데이터를 삭제하시겠습니까?", "삭제", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.No)
                e.Cancel = true;
        }

        private void dataExampleGridView_UserDeletedRow(object sender, DataGridViewRowEventArgs e)
        {
            using (SQLiteCommand cmd = DbConnection.CreateCommand())
            {
                cmd.CommandText = string.Format("DELETE FROM TBL_VOCABULARY_EXAMPLE WHERE idx = {0};", e.Row.Cells[0].Value);
                cmd.ExecuteNonQuery();
            }

            // 데이터를 다시 채운다.
            FillExampleData();
        }

        private void FillExampleData()
        {
            if (EditMode == false)
                return;

            // 전체 행을 삭제합니다.
            dataExampleGridView.Rows.Clear();

            StringBuilder sbDocumentText = new StringBuilder();

            try
            {
                // 데이터를 읽어들입니다.
                string strSQL = string.Format(@"SELECT IDX, VOCABULARY, VOCABULARY_TRANSLATION FROM TBL_VOCABULARY_EXAMPLE WHERE V_IDX={0}", idx);
                SQLiteCommand cmd = new SQLiteCommand(strSQL, DbConnection);
                cmd.CommandType = CommandType.Text;

                using (SQLiteDataReader reader = cmd.ExecuteReader())
                {
                    while (reader.HasRows == true && reader.Read() == true)
                    {
                        string example = reader.GetString(1/*VOCABULARY*/);
                        dataExampleGridView.Rows.Add(reader.GetInt32(0/*IDX*/), example, reader.GetString(2/*VOCABULARY_TRANSLATION*/));

                        if (sbDocumentText.Length != 0)
                            sbDocumentText.Append("<br>");
                        sbDocumentText.Append(example);
                    }
                }
            }
            catch (SQLiteException e)
            {
            }

            exampleWebBrowser.DocumentText = sbDocumentText.ToString();
        }

        private void btnExampleAdd_Click(object sender, EventArgs e)
        {
            string strDocumentText = webBrowser1.DocumentText;
            if (strDocumentText.Length == 0)
            {
                MessageBox.Show("분석할 웹페이지가 로딩되지 않았습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            // 데이터를 파싱하여 예문을 추출한다.
            int first = strDocumentText.IndexOf(@"예문 검색결과");
            if (first == -1)
            {
                MessageBox.Show("읽어들인 웹페이지에서 예문을 찾을 수 없습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            first = strDocumentText.IndexOf(@"class=""exam_result""");
            if (first == -1)
            {
                MessageBox.Show("읽어들인 웹페이지의 파싱이 실패하였습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            first += 20;

            int last = strDocumentText.IndexOf("</dl>", first);
            if (last == -1)
            {
                MessageBox.Show("읽어들인 웹페이지의 파싱이 실패하였습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            strDocumentText = strDocumentText.Substring(first, last - first).Trim();

            string temp;
            List<ExampleInfo> exampleInfoList = new List<ExampleInfo>();

            // DT, DD로 구분
            first = last = 0;
            while (true)
            {
                ExampleInfo exInfo = new ExampleInfo();

                first = strDocumentText.IndexOf(@"<dt>", last);
                if (first == -1)
                {
                    MessageBox.Show("읽어들인 웹페이지의 파싱이 실패하였습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
                last = strDocumentText.IndexOf(@"</dt>", first);
                if (last == -1)
                {
                    MessageBox.Show("읽어들인 웹페이지의 파싱이 실패하였습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
                first += 4;

                temp = strDocumentText.Substring(first, last - first).Trim();
                if (temp.Contains("class='jp'") == true)
                {
                    exInfo.example = extract_example(temp);
                    exInfo.example_translation = "";
                }
                else
                {
                    exInfo.example = "";
                    exInfo.example_translation = extract_translation(temp);
                }

                first = strDocumentText.IndexOf(@"<dd>", last);
                if (first == -1)
                {
                    MessageBox.Show("읽어들인 웹페이지의 파싱이 실패하였습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
                last = strDocumentText.IndexOf(@"</dd>", first);
                if (last == -1)
                {
                    MessageBox.Show("읽어들인 웹페이지의 파싱이 실패하였습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
                first += 4;

                temp = strDocumentText.Substring(first, last - first).Trim();
                if (exInfo.example.Length == 0)
                    exInfo.example = extract_example(temp);
                else
                    exInfo.example_translation = extract_translation(temp);

                exampleInfoList.Add(exInfo);

                temp = strDocumentText.Substring(last + 5).Trim();
                if (temp.Length == 0)
                    break;
            }

            if (exampleInfoList.Count == 0)
            {
                MessageBox.Show("예문이 존재하지 않습니다.", "오류", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            // 예문 대화상자를 연다.
            frmExample form = new frmExample();
            form.idx = idx;
            form.DbConnection = DbConnection;
            form.ExampleInfoList = exampleInfoList;

            if (form.ShowDialog() == DialogResult.OK)
            {
                // 데이터를 다시 채운다.
                FillExampleData();
            }

            form.Dispose();
        }

        private string extract_example(string example)
        {
            int nFirst = 0;
            int nLast = 0;

            // img 태그 제거
            while (true)
            {
                nFirst = example.IndexOf(@"<img", 0);
                if (nFirst == -1)
                    break;
                nLast = example.IndexOf(@"/>", nFirst);
                if (nLast == -1)
                    break;
                nLast += 2;

                example = example.Remove(nFirst, nLast - nFirst).Trim();
            }

            // span 태그 제거
            while (true)
            {
                nFirst = example.IndexOf("\"<span", 0);
                if (nFirst == -1)
                    break;
                nLast = example.IndexOf("/span>\"", nFirst);
                if (nLast == -1)
                    break;
                nLast += 7;

                example = example.Remove(nFirst, nLast - nFirst).Trim();
            }

            // Span 태그 제거
            while (true)
            {
                nFirst = example.IndexOf(@"<span", 0);
                if (nFirst == -1)
                    break;
                nLast = example.IndexOf(@">", nFirst);
                if (nLast == -1)
                    break;
                nLast += 1;

                example = example.Remove(nFirst, nLast - nFirst).Trim();
            }
            example = example.Replace("</span>", "");

            // '→' 이후의 글자 제거
            nFirst = example.IndexOf(@"→", 0);
            if (nFirst != -1)
                example = example.Remove(nFirst, example.Length - nFirst).Trim();

            example = example.Replace("<b>", "");
            example = example.Replace("</b>", "");

            // <sup> 태그에 <font> 태그 추가
            example = example.Replace(@"<sup class=""huri"">", "<sup><font color='#f67474'>");
            example = example.Replace("</sup>", "</font></sup>");

            // ·,- 문자 제거
            example = example.Replace("·", "");
            example = example.Replace("-", "");

            return example;
        }

        private string extract_translation(string mean)
        {
            int nFirst = 0;
            int nLast = 0;

            // img 태그 제거
            while (true)
            {
                nFirst = mean.IndexOf(@"<img", 0);
                if (nFirst == -1)
                    break;
                nLast = mean.IndexOf(@"/>", nFirst);
                if (nLast == -1)
                    break;
                nLast += 2;

                mean = mean.Remove(nFirst, nLast - nFirst).Trim();
            }

            // Span 태그 제거
            while (true)
            {
                nFirst = mean.IndexOf(@"<span", 0);
                if (nFirst == -1)
                    break;
                nLast = mean.IndexOf(@">", nFirst);
                if (nLast == -1)
                    break;
                nLast += 1;

                mean = mean.Remove(nFirst, nLast - nFirst).Trim();
            }
            mean = mean.Replace("</span>", "");

            // '→' 이후의 글자 제거
            nFirst = mean.IndexOf(@"→", 0);
            if (nFirst != -1)
                mean = mean.Remove(nFirst, mean.Length - nFirst).Trim();

            mean = mean.Replace("<b>", "");
            mean = mean.Replace("</b>", "");

            return mean;
        }

        private void btnExampleCustomAdd_Click(object sender, EventArgs e)
        {
            // 예문 대화상자를 연다.
            frmCustomExample form = new frmCustomExample();
            form.idx = idx;
            form.DbConnection = DbConnection;

            if (form.ShowDialog() == DialogResult.OK)
            {
                // 데이터를 다시 채운다.
                FillExampleData();
            }

            form.Dispose();
        }
    }
}
