﻿namespace JapanVocabularyDbManager
{
    partial class frmMain
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
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle9 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle10 = new System.Windows.Forms.DataGridViewCellStyle();
            this.tbpHanja = new System.Windows.Forms.TabPage();
            this.splitContainer2 = new System.Windows.Forms.SplitContainer();
            this.btnHanjaDataAnalyser = new System.Windows.Forms.Button();
            this.btnHanjaAdd = new System.Windows.Forms.Button();
            this.label3 = new System.Windows.Forms.Label();
            this.cboHanjaSearchItem = new System.Windows.Forms.ComboBox();
            this.txtHanjaSearchWord = new System.Windows.Forms.TextBox();
            this.btnHanjaAll = new System.Windows.Forms.Button();
            this.label4 = new System.Windows.Forms.Label();
            this.btnHanjaSearch = new System.Windows.Forms.Button();
            this.dataHanjaGridView = new System.Windows.Forms.DataGridView();
            this.Column9 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column8 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.tbpWord = new System.Windows.Forms.TabPage();
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.btnAddPossibleExampleCountCheck = new System.Windows.Forms.Button();
            this.btnHanjaExistCheck = new System.Windows.Forms.Button();
            this.btnWordAdd = new System.Windows.Forms.Button();
            this.label2 = new System.Windows.Forms.Label();
            this.cboWordSearchItem = new System.Windows.Forms.ComboBox();
            this.txtVocabularySearchWord = new System.Windows.Forms.TextBox();
            this.btnVocabularyShowAll = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.btnVocabularySearch = new System.Windows.Forms.Button();
            this.dataVocabularyGridView = new System.Windows.Forms.DataGridView();
            this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column14 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column15 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column10 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column12 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column13 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column16 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column11 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tbpEtc = new System.Windows.Forms.TabPage();
            this.btnExampleTrimCheck = new System.Windows.Forms.Button();
            this.btnExtractVocabulary = new System.Windows.Forms.Button();
            this.tbpHanja.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer2)).BeginInit();
            this.splitContainer2.Panel1.SuspendLayout();
            this.splitContainer2.Panel2.SuspendLayout();
            this.splitContainer2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dataHanjaGridView)).BeginInit();
            this.tbpWord.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dataVocabularyGridView)).BeginInit();
            this.tabControl1.SuspendLayout();
            this.tbpEtc.SuspendLayout();
            this.SuspendLayout();
            // 
            // tbpHanja
            // 
            this.tbpHanja.Controls.Add(this.splitContainer2);
            this.tbpHanja.Location = new System.Drawing.Point(4, 22);
            this.tbpHanja.Name = "tbpHanja";
            this.tbpHanja.Padding = new System.Windows.Forms.Padding(3);
            this.tbpHanja.Size = new System.Drawing.Size(1285, 801);
            this.tbpHanja.TabIndex = 1;
            this.tbpHanja.Text = "한자";
            this.tbpHanja.UseVisualStyleBackColor = true;
            // 
            // splitContainer2
            // 
            this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
            this.splitContainer2.FixedPanel = System.Windows.Forms.FixedPanel.Panel1;
            this.splitContainer2.IsSplitterFixed = true;
            this.splitContainer2.Location = new System.Drawing.Point(3, 3);
            this.splitContainer2.Name = "splitContainer2";
            this.splitContainer2.Orientation = System.Windows.Forms.Orientation.Horizontal;
            // 
            // splitContainer2.Panel1
            // 
            this.splitContainer2.Panel1.Controls.Add(this.btnHanjaDataAnalyser);
            this.splitContainer2.Panel1.Controls.Add(this.btnHanjaAdd);
            this.splitContainer2.Panel1.Controls.Add(this.label3);
            this.splitContainer2.Panel1.Controls.Add(this.cboHanjaSearchItem);
            this.splitContainer2.Panel1.Controls.Add(this.txtHanjaSearchWord);
            this.splitContainer2.Panel1.Controls.Add(this.btnHanjaAll);
            this.splitContainer2.Panel1.Controls.Add(this.label4);
            this.splitContainer2.Panel1.Controls.Add(this.btnHanjaSearch);
            // 
            // splitContainer2.Panel2
            // 
            this.splitContainer2.Panel2.Controls.Add(this.dataHanjaGridView);
            this.splitContainer2.Size = new System.Drawing.Size(1279, 795);
            this.splitContainer2.SplitterDistance = 33;
            this.splitContainer2.TabIndex = 0;
            // 
            // btnHanjaDataAnalyser
            // 
            this.btnHanjaDataAnalyser.Location = new System.Drawing.Point(802, 4);
            this.btnHanjaDataAnalyser.Name = "btnHanjaDataAnalyser";
            this.btnHanjaDataAnalyser.Size = new System.Drawing.Size(327, 23);
            this.btnHanjaDataAnalyser.TabIndex = 14;
            this.btnHanjaDataAnalyser.Text = "선택된 한자를 네이버 데이터와 비교분석";
            this.btnHanjaDataAnalyser.UseVisualStyleBackColor = true;
            this.btnHanjaDataAnalyser.Click += new System.EventHandler(this.btnHanjaDataAnalyser_Click);
            // 
            // btnHanjaAdd
            // 
            this.btnHanjaAdd.Location = new System.Drawing.Point(601, 6);
            this.btnHanjaAdd.Name = "btnHanjaAdd";
            this.btnHanjaAdd.Size = new System.Drawing.Size(75, 23);
            this.btnHanjaAdd.TabIndex = 13;
            this.btnHanjaAdd.Text = "추가";
            this.btnHanjaAdd.UseVisualStyleBackColor = true;
            this.btnHanjaAdd.Click += new System.EventHandler(this.btnHanjaAdd_Click);
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(10, 11);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(57, 12);
            this.label3.TabIndex = 12;
            this.label3.Text = "검색 항목";
            // 
            // cboHanjaSearchItem
            // 
            this.cboHanjaSearchItem.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboHanjaSearchItem.FormattingEnabled = true;
            this.cboHanjaSearchItem.Items.AddRange(new object[] {
            "한자",
            "음독",
            "훈독",
            "뜻"});
            this.cboHanjaSearchItem.Location = new System.Drawing.Point(67, 7);
            this.cboHanjaSearchItem.Name = "cboHanjaSearchItem";
            this.cboHanjaSearchItem.Size = new System.Drawing.Size(155, 20);
            this.cboHanjaSearchItem.TabIndex = 11;
            // 
            // txtHanjaSearchWord
            // 
            this.txtHanjaSearchWord.Location = new System.Drawing.Point(276, 7);
            this.txtHanjaSearchWord.Name = "txtHanjaSearchWord";
            this.txtHanjaSearchWord.Size = new System.Drawing.Size(144, 21);
            this.txtHanjaSearchWord.TabIndex = 10;
            this.txtHanjaSearchWord.TextChanged += new System.EventHandler(this.txtHanjaSearchWord_TextChanged);
            // 
            // btnHanjaAll
            // 
            this.btnHanjaAll.Location = new System.Drawing.Point(499, 6);
            this.btnHanjaAll.Name = "btnHanjaAll";
            this.btnHanjaAll.Size = new System.Drawing.Size(96, 23);
            this.btnHanjaAll.TabIndex = 9;
            this.btnHanjaAll.Text = "전체검색(&A)";
            this.btnHanjaAll.UseVisualStyleBackColor = true;
            this.btnHanjaAll.Click += new System.EventHandler(this.btnHanjaAll_Click);
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(234, 12);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(41, 12);
            this.label4.TabIndex = 8;
            this.label4.Text = "검색어";
            // 
            // btnHanjaSearch
            // 
            this.btnHanjaSearch.Enabled = false;
            this.btnHanjaSearch.Location = new System.Drawing.Point(423, 6);
            this.btnHanjaSearch.Name = "btnHanjaSearch";
            this.btnHanjaSearch.Size = new System.Drawing.Size(75, 23);
            this.btnHanjaSearch.TabIndex = 7;
            this.btnHanjaSearch.Text = "검색(&S)";
            this.btnHanjaSearch.UseVisualStyleBackColor = true;
            this.btnHanjaSearch.Click += new System.EventHandler(this.btnHanjaSearch_Click);
            // 
            // dataHanjaGridView
            // 
            this.dataHanjaGridView.AllowUserToAddRows = false;
            dataGridViewCellStyle9.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(192)))));
            this.dataHanjaGridView.AlternatingRowsDefaultCellStyle = dataGridViewCellStyle9;
            this.dataHanjaGridView.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.dataHanjaGridView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataHanjaGridView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column9,
            this.Column5,
            this.Column6,
            this.Column7,
            this.Column8});
            this.dataHanjaGridView.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dataHanjaGridView.Location = new System.Drawing.Point(0, 0);
            this.dataHanjaGridView.Name = "dataHanjaGridView";
            this.dataHanjaGridView.ReadOnly = true;
            this.dataHanjaGridView.RowTemplate.Height = 23;
            this.dataHanjaGridView.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataHanjaGridView.Size = new System.Drawing.Size(1279, 758);
            this.dataHanjaGridView.TabIndex = 0;
            this.dataHanjaGridView.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dataHanjaGridView_CellMouseDoubleClick);
            this.dataHanjaGridView.RowPostPaint += new System.Windows.Forms.DataGridViewRowPostPaintEventHandler(this.dataHanjaGridView_RowPostPaint);
            this.dataHanjaGridView.UserDeletedRow += new System.Windows.Forms.DataGridViewRowEventHandler(this.dataHanjaGridView_UserDeletedRow);
            this.dataHanjaGridView.UserDeletingRow += new System.Windows.Forms.DataGridViewRowCancelEventHandler(this.dataHanjaGridView_UserDeletingRow);
            this.dataHanjaGridView.KeyDown += new System.Windows.Forms.KeyEventHandler(this.dataHanjaGridView_KeyDown);
            // 
            // Column9
            // 
            this.Column9.HeaderText = "idx";
            this.Column9.Name = "Column9";
            this.Column9.ReadOnly = true;
            this.Column9.Visible = false;
            // 
            // Column5
            // 
            this.Column5.HeaderText = "한자";
            this.Column5.Name = "Column5";
            this.Column5.ReadOnly = true;
            // 
            // Column6
            // 
            this.Column6.HeaderText = "음독";
            this.Column6.Name = "Column6";
            this.Column6.ReadOnly = true;
            // 
            // Column7
            // 
            this.Column7.HeaderText = "훈독";
            this.Column7.Name = "Column7";
            this.Column7.ReadOnly = true;
            // 
            // Column8
            // 
            this.Column8.HeaderText = "뜻";
            this.Column8.Name = "Column8";
            this.Column8.ReadOnly = true;
            this.Column8.Width = 300;
            // 
            // tbpWord
            // 
            this.tbpWord.Controls.Add(this.splitContainer1);
            this.tbpWord.Location = new System.Drawing.Point(4, 22);
            this.tbpWord.Name = "tbpWord";
            this.tbpWord.Padding = new System.Windows.Forms.Padding(3);
            this.tbpWord.Size = new System.Drawing.Size(1285, 801);
            this.tbpWord.TabIndex = 0;
            this.tbpWord.Text = "단어";
            this.tbpWord.UseVisualStyleBackColor = true;
            // 
            // splitContainer1
            // 
            this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.splitContainer1.FixedPanel = System.Windows.Forms.FixedPanel.Panel1;
            this.splitContainer1.IsSplitterFixed = true;
            this.splitContainer1.Location = new System.Drawing.Point(3, 3);
            this.splitContainer1.Name = "splitContainer1";
            this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
            // 
            // splitContainer1.Panel1
            // 
            this.splitContainer1.Panel1.Controls.Add(this.btnAddPossibleExampleCountCheck);
            this.splitContainer1.Panel1.Controls.Add(this.btnHanjaExistCheck);
            this.splitContainer1.Panel1.Controls.Add(this.btnWordAdd);
            this.splitContainer1.Panel1.Controls.Add(this.label2);
            this.splitContainer1.Panel1.Controls.Add(this.cboWordSearchItem);
            this.splitContainer1.Panel1.Controls.Add(this.txtVocabularySearchWord);
            this.splitContainer1.Panel1.Controls.Add(this.btnVocabularyShowAll);
            this.splitContainer1.Panel1.Controls.Add(this.label1);
            this.splitContainer1.Panel1.Controls.Add(this.btnVocabularySearch);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.Controls.Add(this.dataVocabularyGridView);
            this.splitContainer1.Size = new System.Drawing.Size(1279, 795);
            this.splitContainer1.SplitterDistance = 36;
            this.splitContainer1.TabIndex = 1;
            // 
            // btnAddPossibleExampleCountCheck
            // 
            this.btnAddPossibleExampleCountCheck.Location = new System.Drawing.Point(856, 8);
            this.btnAddPossibleExampleCountCheck.Name = "btnAddPossibleExampleCountCheck";
            this.btnAddPossibleExampleCountCheck.Size = new System.Drawing.Size(210, 23);
            this.btnAddPossibleExampleCountCheck.TabIndex = 8;
            this.btnAddPossibleExampleCountCheck.Text = "추가등록가능예문수확인";
            this.btnAddPossibleExampleCountCheck.UseVisualStyleBackColor = true;
            this.btnAddPossibleExampleCountCheck.Click += new System.EventHandler(this.btnAddPossibleExampleCountCheck_Click);
            // 
            // btnHanjaExistCheck
            // 
            this.btnHanjaExistCheck.Location = new System.Drawing.Point(1072, 8);
            this.btnHanjaExistCheck.Name = "btnHanjaExistCheck";
            this.btnHanjaExistCheck.Size = new System.Drawing.Size(204, 23);
            this.btnHanjaExistCheck.TabIndex = 7;
            this.btnHanjaExistCheck.Text = "모든한자등록여부확인";
            this.btnHanjaExistCheck.UseVisualStyleBackColor = true;
            this.btnHanjaExistCheck.Click += new System.EventHandler(this.btnHanjaExistCheck_Click);
            // 
            // btnWordAdd
            // 
            this.btnWordAdd.Location = new System.Drawing.Point(584, 7);
            this.btnWordAdd.Name = "btnWordAdd";
            this.btnWordAdd.Size = new System.Drawing.Size(75, 23);
            this.btnWordAdd.TabIndex = 6;
            this.btnWordAdd.Text = "추가";
            this.btnWordAdd.UseVisualStyleBackColor = true;
            this.btnWordAdd.Click += new System.EventHandler(this.btnWordAdd_Click);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(0, 13);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(53, 12);
            this.label2.TabIndex = 5;
            this.label2.Text = "검색항목";
            // 
            // cboWordSearchItem
            // 
            this.cboWordSearchItem.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboWordSearchItem.FormattingEnabled = true;
            this.cboWordSearchItem.Items.AddRange(new object[] {
            "단어",
            "히라가나/가타가나",
            "설명"});
            this.cboWordSearchItem.Location = new System.Drawing.Point(54, 8);
            this.cboWordSearchItem.Name = "cboWordSearchItem";
            this.cboWordSearchItem.Size = new System.Drawing.Size(114, 20);
            this.cboWordSearchItem.TabIndex = 4;
            // 
            // txtVocabularySearchWord
            // 
            this.txtVocabularySearchWord.Location = new System.Drawing.Point(235, 8);
            this.txtVocabularySearchWord.Name = "txtVocabularySearchWord";
            this.txtVocabularySearchWord.Size = new System.Drawing.Size(144, 21);
            this.txtVocabularySearchWord.TabIndex = 3;
            this.txtVocabularySearchWord.TextChanged += new System.EventHandler(this.txtVocabularySearchWord_TextChanged);
            this.txtVocabularySearchWord.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.txtVocabularySearchWord_KeyPress);
            // 
            // btnVocabularyShowAll
            // 
            this.btnVocabularyShowAll.Location = new System.Drawing.Point(460, 7);
            this.btnVocabularyShowAll.Name = "btnVocabularyShowAll";
            this.btnVocabularyShowAll.Size = new System.Drawing.Size(95, 23);
            this.btnVocabularyShowAll.TabIndex = 2;
            this.btnVocabularyShowAll.Text = "전체검색(&A)";
            this.btnVocabularyShowAll.UseVisualStyleBackColor = true;
            this.btnVocabularyShowAll.Click += new System.EventHandler(this.btnVocabularyShowAll_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(193, 13);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(41, 12);
            this.label1.TabIndex = 1;
            this.label1.Text = "검색어";
            // 
            // btnVocabularySearch
            // 
            this.btnVocabularySearch.Enabled = false;
            this.btnVocabularySearch.Location = new System.Drawing.Point(384, 7);
            this.btnVocabularySearch.Name = "btnVocabularySearch";
            this.btnVocabularySearch.Size = new System.Drawing.Size(75, 23);
            this.btnVocabularySearch.TabIndex = 0;
            this.btnVocabularySearch.Text = "검색(&S)";
            this.btnVocabularySearch.UseVisualStyleBackColor = true;
            this.btnVocabularySearch.Click += new System.EventHandler(this.btnVocabularySearch_Click);
            // 
            // dataVocabularyGridView
            // 
            this.dataVocabularyGridView.AllowUserToAddRows = false;
            this.dataVocabularyGridView.AllowUserToDeleteRows = false;
            dataGridViewCellStyle10.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(192)))));
            this.dataVocabularyGridView.AlternatingRowsDefaultCellStyle = dataGridViewCellStyle10;
            this.dataVocabularyGridView.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.dataVocabularyGridView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataVocabularyGridView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column4,
            this.Column1,
            this.Column2,
            this.Column3,
            this.Column14,
            this.Column15,
            this.Column10,
            this.Column12,
            this.Column13,
            this.Column16,
            this.Column11});
            this.dataVocabularyGridView.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dataVocabularyGridView.Location = new System.Drawing.Point(0, 0);
            this.dataVocabularyGridView.Name = "dataVocabularyGridView";
            this.dataVocabularyGridView.ReadOnly = true;
            this.dataVocabularyGridView.RowTemplate.Height = 23;
            this.dataVocabularyGridView.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataVocabularyGridView.Size = new System.Drawing.Size(1279, 755);
            this.dataVocabularyGridView.TabIndex = 2;
            this.dataVocabularyGridView.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dataVocabularyGridView_CellMouseDoubleClick);
            this.dataVocabularyGridView.RowPostPaint += new System.Windows.Forms.DataGridViewRowPostPaintEventHandler(this.dataVocabularyGridView_RowPostPaint);
            this.dataVocabularyGridView.KeyDown += new System.Windows.Forms.KeyEventHandler(this.dataVocabularyGridView_KeyDown);
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
            this.Column1.HeaderText = "단어";
            this.Column1.Name = "Column1";
            this.Column1.ReadOnly = true;
            // 
            // Column2
            // 
            this.Column2.HeaderText = "히라가나/가타카나";
            this.Column2.Name = "Column2";
            this.Column2.ReadOnly = true;
            this.Column2.Width = 200;
            // 
            // Column3
            // 
            this.Column3.HeaderText = "설명";
            this.Column3.Name = "Column3";
            this.Column3.ReadOnly = true;
            this.Column3.Width = 200;
            // 
            // Column14
            // 
            this.Column14.HeaderText = "품사";
            this.Column14.Name = "Column14";
            this.Column14.ReadOnly = true;
            // 
            // Column15
            // 
            this.Column15.HeaderText = "등급";
            this.Column15.Name = "Column15";
            this.Column15.ReadOnly = true;
            // 
            // Column10
            // 
            this.Column10.HeaderText = "예문개수";
            this.Column10.Name = "Column10";
            this.Column10.ReadOnly = true;
            this.Column10.Width = 90;
            // 
            // Column12
            // 
            this.Column12.HeaderText = "사용유무";
            this.Column12.Name = "Column12";
            this.Column12.ReadOnly = true;
            this.Column12.Width = 80;
            // 
            // Column13
            // 
            this.Column13.HeaderText = "";
            this.Column13.Name = "Column13";
            this.Column13.ReadOnly = true;
            // 
            // Column16
            // 
            this.Column16.HeaderText = "wordClassCode";
            this.Column16.Name = "Column16";
            this.Column16.ReadOnly = true;
            this.Column16.Visible = false;
            // 
            // Column11
            // 
            this.Column11.HeaderText = "jlptClassCode";
            this.Column11.Name = "Column11";
            this.Column11.ReadOnly = true;
            this.Column11.Visible = false;
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tbpWord);
            this.tabControl1.Controls.Add(this.tbpHanja);
            this.tabControl1.Controls.Add(this.tbpEtc);
            this.tabControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControl1.Location = new System.Drawing.Point(5, 5);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.RightToLeftLayout = true;
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(1293, 827);
            this.tabControl1.TabIndex = 0;
            // 
            // tbpEtc
            // 
            this.tbpEtc.Controls.Add(this.btnExampleTrimCheck);
            this.tbpEtc.Controls.Add(this.btnExtractVocabulary);
            this.tbpEtc.Location = new System.Drawing.Point(4, 22);
            this.tbpEtc.Name = "tbpEtc";
            this.tbpEtc.Padding = new System.Windows.Forms.Padding(3);
            this.tbpEtc.Size = new System.Drawing.Size(1285, 801);
            this.tbpEtc.TabIndex = 2;
            this.tbpEtc.Text = "기타";
            this.tbpEtc.UseVisualStyleBackColor = true;
            // 
            // btnExampleTrimCheck
            // 
            this.btnExampleTrimCheck.Location = new System.Drawing.Point(19, 48);
            this.btnExampleTrimCheck.Name = "btnExampleTrimCheck";
            this.btnExampleTrimCheck.Size = new System.Drawing.Size(529, 23);
            this.btnExampleTrimCheck.TabIndex = 1;
            this.btnExampleTrimCheck.Text = "전체 예문중에서 앞뒤에 공백있는 예문 있는지 확인";
            this.btnExampleTrimCheck.UseVisualStyleBackColor = true;
            this.btnExampleTrimCheck.Click += new System.EventHandler(this.btnExampleTrimCheck_Click);
            // 
            // btnExtractVocabulary
            // 
            this.btnExtractVocabulary.Location = new System.Drawing.Point(19, 19);
            this.btnExtractVocabulary.Name = "btnExtractVocabulary";
            this.btnExtractVocabulary.Size = new System.Drawing.Size(529, 23);
            this.btnExtractVocabulary.TabIndex = 0;
            this.btnExtractVocabulary.Text = "네이버에서 N1~N5 단어 추출해서 TBL_EXTRACT_VOCABULARY에 넣기";
            this.btnExtractVocabulary.UseVisualStyleBackColor = true;
            this.btnExtractVocabulary.Click += new System.EventHandler(this.btnExtractVocabulary_Click);
            // 
            // frmMain
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1303, 837);
            this.Controls.Add(this.tabControl1);
            this.Name = "frmMain";
            this.Padding = new System.Windows.Forms.Padding(5);
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "일본어 단어DB 관리자";
            this.WindowState = System.Windows.Forms.FormWindowState.Maximized;
            this.Load += new System.EventHandler(this.frmMain_Load);
            this.tbpHanja.ResumeLayout(false);
            this.splitContainer2.Panel1.ResumeLayout(false);
            this.splitContainer2.Panel1.PerformLayout();
            this.splitContainer2.Panel2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer2)).EndInit();
            this.splitContainer2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dataHanjaGridView)).EndInit();
            this.tbpWord.ResumeLayout(false);
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel1.PerformLayout();
            this.splitContainer1.Panel2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
            this.splitContainer1.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dataVocabularyGridView)).EndInit();
            this.tabControl1.ResumeLayout(false);
            this.tbpEtc.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TabPage tbpHanja;
        private System.Windows.Forms.TabPage tbpWord;
        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.Button btnWordAdd;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.ComboBox cboWordSearchItem;
        private System.Windows.Forms.TextBox txtVocabularySearchWord;
        private System.Windows.Forms.Button btnVocabularyShowAll;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button btnVocabularySearch;
        private System.Windows.Forms.DataGridView dataVocabularyGridView;
        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.SplitContainer splitContainer2;
        private System.Windows.Forms.DataGridView dataHanjaGridView;
        private System.Windows.Forms.Button btnHanjaAdd;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.ComboBox cboHanjaSearchItem;
        private System.Windows.Forms.TextBox txtHanjaSearchWord;
        private System.Windows.Forms.Button btnHanjaAll;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Button btnHanjaSearch;
        private System.Windows.Forms.Button btnHanjaDataAnalyser;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column9;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column5;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column6;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column7;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column8;
        private System.Windows.Forms.Button btnHanjaExistCheck;
        private System.Windows.Forms.Button btnAddPossibleExampleCountCheck;
        private System.Windows.Forms.TabPage tbpEtc;
        private System.Windows.Forms.Button btnExtractVocabulary;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column14;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column15;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column10;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column12;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column13;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column16;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column11;
        private System.Windows.Forms.Button btnExampleTrimCheck;

    }
}
