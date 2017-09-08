﻿namespace JapanVocabularyDbManager
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

        #region Windows Form Designer generated wordClassCode

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the wordClassCode editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.btnAddPossibleExample = new System.Windows.Forms.Button();
            this.btnExampleCustomAdd = new System.Windows.Forms.Button();
            this.exampleWebBrowser = new System.Windows.Forms.WebBrowser();
            this.btnAddNoClose = new System.Windows.Forms.Button();
            this.btnExampleAdd = new System.Windows.Forms.Button();
            this.dataExampleGridView = new System.Windows.Forms.DataGridView();
            this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.clbWordClassListBox = new System.Windows.Forms.CheckedListBox();
            this.clbJlptClassListBox = new System.Windows.Forms.CheckedListBox();
            this.label6 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
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
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dataExampleGridView)).BeginInit();
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
            this.splitContainer1.Panel1.Controls.Add(this.btnAddPossibleExample);
            this.splitContainer1.Panel1.Controls.Add(this.btnExampleCustomAdd);
            this.splitContainer1.Panel1.Controls.Add(this.exampleWebBrowser);
            this.splitContainer1.Panel1.Controls.Add(this.btnAddNoClose);
            this.splitContainer1.Panel1.Controls.Add(this.btnExampleAdd);
            this.splitContainer1.Panel1.Controls.Add(this.dataExampleGridView);
            this.splitContainer1.Panel1.Controls.Add(this.groupBox1);
            this.splitContainer1.Panel1.Controls.Add(this.btnOk);
            this.splitContainer1.Panel1.Controls.Add(this.btnCancel);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.Controls.Add(this.webBrowser1);
            this.splitContainer1.Size = new System.Drawing.Size(1396, 882);
            this.splitContainer1.SplitterDistance = 494;
            this.splitContainer1.TabIndex = 0;
            // 
            // btnAddPossibleExample
            // 
            this.btnAddPossibleExample.Location = new System.Drawing.Point(389, 788);
            this.btnAddPossibleExample.Name = "btnAddPossibleExample";
            this.btnAddPossibleExample.Size = new System.Drawing.Size(96, 42);
            this.btnAddPossibleExample.TabIndex = 19;
            this.btnAddPossibleExample.Text = "추가등록가능 예문추가";
            this.btnAddPossibleExample.UseVisualStyleBackColor = true;
            this.btnAddPossibleExample.Click += new System.EventHandler(this.btnAddPossibleExample_Click);
            // 
            // btnExampleCustomAdd
            // 
            this.btnExampleCustomAdd.Location = new System.Drawing.Point(388, 758);
            this.btnExampleCustomAdd.Name = "btnExampleCustomAdd";
            this.btnExampleCustomAdd.Size = new System.Drawing.Size(99, 23);
            this.btnExampleCustomAdd.TabIndex = 18;
            this.btnExampleCustomAdd.Text = "예문수동추가";
            this.btnExampleCustomAdd.UseVisualStyleBackColor = true;
            this.btnExampleCustomAdd.Click += new System.EventHandler(this.btnExampleCustomAdd_Click);
            // 
            // exampleWebBrowser
            // 
            this.exampleWebBrowser.Location = new System.Drawing.Point(5, 729);
            this.exampleWebBrowser.MinimumSize = new System.Drawing.Size(20, 20);
            this.exampleWebBrowser.Name = "exampleWebBrowser";
            this.exampleWebBrowser.Size = new System.Drawing.Size(377, 146);
            this.exampleWebBrowser.TabIndex = 17;
            // 
            // btnAddNoClose
            // 
            this.btnAddNoClose.Location = new System.Drawing.Point(205, 480);
            this.btnAddNoClose.Name = "btnAddNoClose";
            this.btnAddNoClose.Size = new System.Drawing.Size(75, 23);
            this.btnAddNoClose.TabIndex = 16;
            this.btnAddNoClose.Text = "단어 저장";
            this.btnAddNoClose.UseVisualStyleBackColor = true;
            this.btnAddNoClose.Click += new System.EventHandler(this.btnAddNoClose_Click);
            // 
            // btnExampleAdd
            // 
            this.btnExampleAdd.Location = new System.Drawing.Point(388, 729);
            this.btnExampleAdd.Name = "btnExampleAdd";
            this.btnExampleAdd.Size = new System.Drawing.Size(99, 23);
            this.btnExampleAdd.TabIndex = 15;
            this.btnExampleAdd.Text = "예문자동추가";
            this.btnExampleAdd.UseVisualStyleBackColor = true;
            this.btnExampleAdd.Click += new System.EventHandler(this.btnExampleAdd_Click);
            // 
            // dataExampleGridView
            // 
            this.dataExampleGridView.AllowUserToAddRows = false;
            this.dataExampleGridView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataExampleGridView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column4,
            this.Column1,
            this.Column2});
            this.dataExampleGridView.Location = new System.Drawing.Point(5, 509);
            this.dataExampleGridView.MultiSelect = false;
            this.dataExampleGridView.Name = "dataExampleGridView";
            this.dataExampleGridView.ReadOnly = true;
            this.dataExampleGridView.RowTemplate.Height = 23;
            this.dataExampleGridView.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataExampleGridView.Size = new System.Drawing.Size(482, 214);
            this.dataExampleGridView.TabIndex = 14;
            this.dataExampleGridView.UserDeletedRow += new System.Windows.Forms.DataGridViewRowEventHandler(this.dataExampleGridView_UserDeletedRow);
            this.dataExampleGridView.UserDeletingRow += new System.Windows.Forms.DataGridViewRowCancelEventHandler(this.dataExampleGridView_UserDeletingRow);
            // 
            // Column4
            // 
            this.Column4.HeaderText = "idx";
            this.Column4.Name = "Column4";
            this.Column4.ReadOnly = true;
            this.Column4.Visible = false;
            // 
            // Column1
            // 
            this.Column1.HeaderText = "예문";
            this.Column1.Name = "Column1";
            this.Column1.ReadOnly = true;
            this.Column1.Width = 200;
            // 
            // Column2
            // 
            this.Column2.HeaderText = "해석";
            this.Column2.Name = "Column2";
            this.Column2.ReadOnly = true;
            this.Column2.Width = 200;
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.clbWordClassListBox);
            this.groupBox1.Controls.Add(this.clbJlptClassListBox);
            this.groupBox1.Controls.Add(this.label6);
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
            this.groupBox1.Size = new System.Drawing.Size(488, 471);
            this.groupBox1.TabIndex = 13;
            this.groupBox1.TabStop = false;
            // 
            // clbWordClassListBox
            // 
            this.clbWordClassListBox.CheckOnClick = true;
            this.clbWordClassListBox.FormattingEnabled = true;
            this.clbWordClassListBox.Items.AddRange(new object[] {
            "명사",
            "대명사",
            "동사",
            "조사",
            "형용사",
            "접사",
            "부사",
            "감동사",
            "형용동사",
            "기타"});
            this.clbWordClassListBox.Location = new System.Drawing.Point(78, 221);
            this.clbWordClassListBox.MultiColumn = true;
            this.clbWordClassListBox.Name = "clbWordClassListBox";
            this.clbWordClassListBox.Size = new System.Drawing.Size(404, 100);
            this.clbWordClassListBox.TabIndex = 15;
            // 
            // clbJlptClassListBox
            // 
            this.clbJlptClassListBox.CheckOnClick = true;
            this.clbJlptClassListBox.FormattingEnabled = true;
            this.clbJlptClassListBox.Items.AddRange(new object[] {
            "N1",
            "N2",
            "N3",
            "N4",
            "N5",
            "미분류"});
            this.clbJlptClassListBox.Location = new System.Drawing.Point(78, 147);
            this.clbJlptClassListBox.MultiColumn = true;
            this.clbJlptClassListBox.Name = "clbJlptClassListBox";
            this.clbJlptClassListBox.Size = new System.Drawing.Size(404, 68);
            this.clbJlptClassListBox.TabIndex = 14;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(46, 221);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(29, 12);
            this.label6.TabIndex = 13;
            this.label6.Text = "품사";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(10, 147);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(62, 12);
            this.label5.TabIndex = 12;
            this.label5.Text = "JLPT 레벨";
            // 
            // btnAddHanja
            // 
            this.btnAddHanja.Location = new System.Drawing.Point(9, 402);
            this.btnAddHanja.Name = "btnAddHanja";
            this.btnAddHanja.Size = new System.Drawing.Size(66, 63);
            this.btnAddHanja.TabIndex = 11;
            this.btnAddHanja.Text = "한자 추가";
            this.btnAddHanja.UseVisualStyleBackColor = true;
            this.btnAddHanja.Click += new System.EventHandler(this.btnAddHanja_Click);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(-11, 50);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(83, 12);
            this.label2.TabIndex = 1;
            this.label2.Text = "히라가나/가타";
            this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(43, 23);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(29, 12);
            this.label1.TabIndex = 0;
            this.label1.Text = "단어";
            this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(43, 77);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(29, 12);
            this.label3.TabIndex = 2;
            this.label3.Text = "설명";
            this.label3.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // txtExtensionInfo
            // 
            this.txtExtensionInfo.Location = new System.Drawing.Point(78, 330);
            this.txtExtensionInfo.Multiline = true;
            this.txtExtensionInfo.Name = "txtExtensionInfo";
            this.txtExtensionInfo.ReadOnly = true;
            this.txtExtensionInfo.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.txtExtensionInfo.Size = new System.Drawing.Size(404, 138);
            this.txtExtensionInfo.TabIndex = 7;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(15, 330);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(57, 12);
            this.label4.TabIndex = 3;
            this.label4.Text = "확장 정보";
            this.label4.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // txtVocabularyTranslation
            // 
            this.txtVocabularyTranslation.Location = new System.Drawing.Point(78, 74);
            this.txtVocabularyTranslation.Multiline = true;
            this.txtVocabularyTranslation.Name = "txtVocabularyTranslation";
            this.txtVocabularyTranslation.Size = new System.Drawing.Size(404, 67);
            this.txtVocabularyTranslation.TabIndex = 6;
            this.txtVocabularyTranslation.Leave += new System.EventHandler(this.txtDescription_Leave);
            // 
            // txtVocabulary
            // 
            this.txtVocabulary.Location = new System.Drawing.Point(78, 20);
            this.txtVocabulary.Name = "txtVocabulary";
            this.txtVocabulary.Size = new System.Drawing.Size(404, 21);
            this.txtVocabulary.TabIndex = 4;
            this.txtVocabulary.TextChanged += new System.EventHandler(this.txtVocabulary_TextChanged);
            this.txtVocabulary.Leave += new System.EventHandler(this.txtVocabulary_Leave);
            // 
            // txtVocabularyGana
            // 
            this.txtVocabularyGana.Location = new System.Drawing.Point(78, 47);
            this.txtVocabularyGana.Name = "txtVocabularyGana";
            this.txtVocabularyGana.Size = new System.Drawing.Size(404, 21);
            this.txtVocabularyGana.TabIndex = 5;
            this.txtVocabularyGana.TextChanged += new System.EventHandler(this.txtHiGaVocabulary_TextChanged);
            this.txtVocabularyGana.Leave += new System.EventHandler(this.txtHiGaVocabulary_Leave);
            // 
            // btnOk
            // 
            this.btnOk.Location = new System.Drawing.Point(286, 480);
            this.btnOk.Name = "btnOk";
            this.btnOk.Size = new System.Drawing.Size(120, 23);
            this.btnOk.TabIndex = 12;
            this.btnOk.Text = "단어 저장 후 닫기";
            this.btnOk.UseVisualStyleBackColor = true;
            this.btnOk.Click += new System.EventHandler(this.btnOk_Click);
            // 
            // btnCancel
            // 
            this.btnCancel.Location = new System.Drawing.Point(412, 480);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(75, 23);
            this.btnCancel.TabIndex = 11;
            this.btnCancel.Text = "그냥 닫기";
            this.btnCancel.UseVisualStyleBackColor = true;
            this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
            // 
            // webBrowser1
            // 
            this.webBrowser1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.webBrowser1.Location = new System.Drawing.Point(0, 0);
            this.webBrowser1.MinimumSize = new System.Drawing.Size(20, 20);
            this.webBrowser1.Name = "webBrowser1";
            this.webBrowser1.Size = new System.Drawing.Size(898, 882);
            this.webBrowser1.TabIndex = 0;
            this.webBrowser1.Url = new System.Uri("http://jpdic.naver.com/", System.UriKind.Absolute);
            // 
            // frmVocabulary
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1396, 882);
            this.Controls.Add(this.splitContainer1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Name = "frmVocabulary";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "단어";
            this.Load += new System.EventHandler(this.frmVocabulary_Load);
            this.Shown += new System.EventHandler(this.frmVocabulary_Shown);
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
            this.splitContainer1.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dataExampleGridView)).EndInit();
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
        private System.Windows.Forms.DataGridView dataExampleGridView;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
        private System.Windows.Forms.Button btnExampleAdd;
        private System.Windows.Forms.Button btnAddNoClose;
        private System.Windows.Forms.WebBrowser exampleWebBrowser;
        private System.Windows.Forms.Button btnExampleCustomAdd;
        private System.Windows.Forms.Button btnAddPossibleExample;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.CheckedListBox clbJlptClassListBox;
        private System.Windows.Forms.CheckedListBox clbWordClassListBox;

    }
}