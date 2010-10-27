namespace JapanWordManager
{
    partial class frmVocabulary
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.btnAddHanja = new System.Windows.Forms.Button();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.txtExtensionInfo = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.txtVocabularyTranslation = new System.Windows.Forms.TextBox();
            this.txtVocabulary = new System.Windows.Forms.TextBox();
            this.txtVocabularyGana = new System.Windows.Forms.TextBox();
            this.btnOk = new System.Windows.Forms.Button();
            this.btnCancel = new System.Windows.Forms.Button();
            this.webBrowser1 = new System.Windows.Forms.WebBrowser();
            this.label5 = new System.Windows.Forms.Label();
            this.cboPartsOfSpeech = new System.Windows.Forms.ComboBox();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // splitContainer1
            // 
            this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.splitContainer1.FixedPanel = System.Windows.Forms.FixedPanel.Panel1;
            this.splitContainer1.IsSplitterFixed = true;
            this.splitContainer1.Location = new System.Drawing.Point(0, 0);
            this.splitContainer1.Name = "splitContainer1";
            // 
            // splitContainer1.Panel1
            // 
            this.splitContainer1.Panel1.Controls.Add(this.groupBox1);
            this.splitContainer1.Panel1.Controls.Add(this.btnOk);
            this.splitContainer1.Panel1.Controls.Add(this.btnCancel);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.Controls.Add(this.webBrowser1);
            this.splitContainer1.Size = new System.Drawing.Size(1202, 622);
            this.splitContainer1.SplitterDistance = 494;
            this.splitContainer1.TabIndex = 0;
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.cboPartsOfSpeech);
            this.groupBox1.Controls.Add(this.label5);
            this.groupBox1.Controls.Add(this.btnAddHanja);
            this.groupBox1.Controls.Add(this.label2);
            this.groupBox1.Controls.Add(this.label1);
            this.groupBox1.Controls.Add(this.label3);
            this.groupBox1.Controls.Add(this.txtExtensionInfo);
            this.groupBox1.Controls.Add(this.label4);
            this.groupBox1.Controls.Add(this.txtVocabularyTranslation);
            this.groupBox1.Controls.Add(this.txtVocabulary);
            this.groupBox1.Controls.Add(this.txtVocabularyGana);
            this.groupBox1.Location = new System.Drawing.Point(3, 3);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(488, 415);
            this.groupBox1.TabIndex = 13;
            this.groupBox1.TabStop = false;
            // 
            // btnAddHanja
            // 
            this.btnAddHanja.Location = new System.Drawing.Point(20, 381);
            this.btnAddHanja.Name = "btnAddHanja";
            this.btnAddHanja.Size = new System.Drawing.Size(100, 28);
            this.btnAddHanja.TabIndex = 11;
            this.btnAddHanja.Text = "한자 추가";
            this.btnAddHanja.UseVisualStyleBackColor = true;
            this.btnAddHanja.Click += new System.EventHandler(this.btnAddHanja_Click);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(13, 50);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(107, 12);
            this.label2.TabIndex = 1;
            this.label2.Text = "히라가나/가타카나";
            this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(91, 23);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(29, 12);
            this.label1.TabIndex = 0;
            this.label1.Text = "단어";
            this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(91, 77);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(29, 12);
            this.label3.TabIndex = 2;
            this.label3.Text = "설명";
            this.label3.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // txtExtensionInfo
            // 
            this.txtExtensionInfo.Location = new System.Drawing.Point(126, 194);
            this.txtExtensionInfo.Multiline = true;
            this.txtExtensionInfo.Name = "txtExtensionInfo";
            this.txtExtensionInfo.ReadOnly = true;
            this.txtExtensionInfo.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.txtExtensionInfo.Size = new System.Drawing.Size(356, 215);
            this.txtExtensionInfo.TabIndex = 7;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(63, 197);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(57, 12);
            this.label4.TabIndex = 3;
            this.label4.Text = "확장 정보";
            this.label4.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // txtDescription
            // 
            this.txtVocabularyTranslation.Location = new System.Drawing.Point(126, 74);
            this.txtVocabularyTranslation.Multiline = true;
            this.txtVocabularyTranslation.Name = "txtDescription";
            this.txtVocabularyTranslation.Size = new System.Drawing.Size(356, 87);
            this.txtVocabularyTranslation.TabIndex = 6;
            this.txtVocabularyTranslation.Leave += new System.EventHandler(this.txtDescription_Leave);
            // 
            // txtVocabulary
            // 
            this.txtVocabulary.Location = new System.Drawing.Point(126, 20);
            this.txtVocabulary.Name = "txtVocabulary";
            this.txtVocabulary.Size = new System.Drawing.Size(356, 21);
            this.txtVocabulary.TabIndex = 4;
            this.txtVocabulary.TextChanged += new System.EventHandler(this.txtVocabulary_TextChanged);
            this.txtVocabulary.Leave += new System.EventHandler(this.txtVocabulary_Leave);
            // 
            // txtHiGaVocabulary
            // 
            this.txtVocabularyGana.Location = new System.Drawing.Point(126, 47);
            this.txtVocabularyGana.Name = "txtHiGaVocabulary";
            this.txtVocabularyGana.Size = new System.Drawing.Size(356, 21);
            this.txtVocabularyGana.TabIndex = 5;
            this.txtVocabularyGana.TextChanged += new System.EventHandler(this.txtHiGaVocabulary_TextChanged);
            this.txtVocabularyGana.Leave += new System.EventHandler(this.txtHiGaVocabulary_Leave);
            // 
            // btnOk
            // 
            this.btnOk.Location = new System.Drawing.Point(336, 424);
            this.btnOk.Name = "btnOk";
            this.btnOk.Size = new System.Drawing.Size(75, 23);
            this.btnOk.TabIndex = 12;
            this.btnOk.Text = "완료";
            this.btnOk.UseVisualStyleBackColor = true;
            this.btnOk.Click += new System.EventHandler(this.btnOk_Click);
            // 
            // btnCancel
            // 
            this.btnCancel.Location = new System.Drawing.Point(417, 424);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(75, 23);
            this.btnCancel.TabIndex = 11;
            this.btnCancel.Text = "취소";
            this.btnCancel.UseVisualStyleBackColor = true;
            this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
            // 
            // webBrowser1
            // 
            this.webBrowser1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.webBrowser1.Location = new System.Drawing.Point(0, 0);
            this.webBrowser1.MinimumSize = new System.Drawing.Size(20, 20);
            this.webBrowser1.Name = "webBrowser1";
            this.webBrowser1.Size = new System.Drawing.Size(704, 622);
            this.webBrowser1.TabIndex = 0;
            this.webBrowser1.Url = new System.Uri("http://jpdic.naver.com/", System.UriKind.Absolute);
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(91, 173);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(29, 12);
            this.label5.TabIndex = 12;
            this.label5.Text = "품사";
            this.label5.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // cboPartsOfSpeech
            // 
            this.cboPartsOfSpeech.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboPartsOfSpeech.FormattingEnabled = true;
            this.cboPartsOfSpeech.Location = new System.Drawing.Point(127, 168);
            this.cboPartsOfSpeech.Name = "cboPartsOfSpeech";
            this.cboPartsOfSpeech.Size = new System.Drawing.Size(355, 20);
            this.cboPartsOfSpeech.TabIndex = 13;
            // 
            // frmVocabulary
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1202, 622);
            this.Controls.Add(this.splitContainer1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Name = "frmVocabulary";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "단어";
            this.Load += new System.EventHandler(this.frmVocabulary_Load);
            this.Shown += new System.EventHandler(this.frmVocabulary_Shown);
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel2.ResumeLayout(false);
            this.splitContainer1.ResumeLayout(false);
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Button btnAddHanja;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox txtExtensionInfo;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.TextBox txtVocabularyTranslation;
        private System.Windows.Forms.TextBox txtVocabulary;
        private System.Windows.Forms.TextBox txtVocabularyGana;
        private System.Windows.Forms.Button btnOk;
        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.WebBrowser webBrowser1;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.ComboBox cboPartsOfSpeech;

    }
}