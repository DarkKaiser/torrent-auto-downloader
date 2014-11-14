namespace JapanVocabularyDbManager
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
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
            this.tbpHanja = new System.Windows.Forms.TabPage();
            this.splitContainer2 = new System.Windows.Forms.SplitContainer();
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
            this.Column11 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.tbpWord = new System.Windows.Forms.TabPage();
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.btnWordAdd = new System.Windows.Forms.Button();
            this.label2 = new System.Windows.Forms.Label();
            this.cboWordSearchItem = new System.Windows.Forms.ComboBox();
            this.txtWordSearchWord = new System.Windows.Forms.TextBox();
            this.btnWordAll = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.btnWordSearch = new System.Windows.Forms.Button();
            this.dataWordGridView = new System.Windows.Forms.DataGridView();
            this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column10 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.tabControl1 = new System.Windows.Forms.TabControl();
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
            ((System.ComponentModel.ISupportInitialize)(this.dataWordGridView)).BeginInit();
            this.tabControl1.SuspendLayout();
            this.SuspendLayout();
            // 
            // tbpHanja
            // 
            this.tbpHanja.Controls.Add(this.splitContainer2);
            this.tbpHanja.Location = new System.Drawing.Point(4, 22);
            this.tbpHanja.Name = "tbpHanja";
            this.tbpHanja.Padding = new System.Windows.Forms.Padding(3);
            this.tbpHanja.Size = new System.Drawing.Size(1045, 688);
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
            this.splitContainer2.Size = new System.Drawing.Size(1039, 682);
            this.splitContainer2.SplitterDistance = 33;
            this.splitContainer2.TabIndex = 0;
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
            this.btnHanjaAll.Size = new System.Drawing.Size(75, 23);
            this.btnHanjaAll.TabIndex = 9;
            this.btnHanjaAll.Text = "전체(&A)";
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
            dataGridViewCellStyle1.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(192)))));
            this.dataHanjaGridView.AlternatingRowsDefaultCellStyle = dataGridViewCellStyle1;
            this.dataHanjaGridView.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.dataHanjaGridView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataHanjaGridView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column9,
            this.Column5,
            this.Column6,
            this.Column7,
            this.Column8,
            this.Column11});
            this.dataHanjaGridView.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dataHanjaGridView.Location = new System.Drawing.Point(0, 0);
            this.dataHanjaGridView.MultiSelect = false;
            this.dataHanjaGridView.Name = "dataHanjaGridView";
            this.dataHanjaGridView.ReadOnly = true;
            this.dataHanjaGridView.RowTemplate.Height = 23;
            this.dataHanjaGridView.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataHanjaGridView.Size = new System.Drawing.Size(1039, 645);
            this.dataHanjaGridView.TabIndex = 0;
            this.dataHanjaGridView.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dataHanjaGridView_CellMouseDoubleClick);
            this.dataHanjaGridView.RowPostPaint += new System.Windows.Forms.DataGridViewRowPostPaintEventHandler(this.dataHanjaGridView_RowPostPaint);
            this.dataHanjaGridView.UserDeletedRow += new System.Windows.Forms.DataGridViewRowEventHandler(this.dataHanjaGridView_UserDeletedRow);
            this.dataHanjaGridView.UserDeletingRow += new System.Windows.Forms.DataGridViewRowCancelEventHandler(this.dataHanjaGridView_UserDeletingRow);
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
            // Column11
            // 
            this.Column11.HeaderText = "JLPT 급수";
            this.Column11.Name = "Column11";
            this.Column11.ReadOnly = true;
            // 
            // tbpWord
            // 
            this.tbpWord.Controls.Add(this.splitContainer1);
            this.tbpWord.Location = new System.Drawing.Point(4, 22);
            this.tbpWord.Name = "tbpWord";
            this.tbpWord.Padding = new System.Windows.Forms.Padding(3);
            this.tbpWord.Size = new System.Drawing.Size(1064, 436);
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
            this.splitContainer1.Panel1.Controls.Add(this.btnWordAdd);
            this.splitContainer1.Panel1.Controls.Add(this.label2);
            this.splitContainer1.Panel1.Controls.Add(this.cboWordSearchItem);
            this.splitContainer1.Panel1.Controls.Add(this.txtWordSearchWord);
            this.splitContainer1.Panel1.Controls.Add(this.btnWordAll);
            this.splitContainer1.Panel1.Controls.Add(this.label1);
            this.splitContainer1.Panel1.Controls.Add(this.btnWordSearch);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.Controls.Add(this.dataWordGridView);
            this.splitContainer1.Size = new System.Drawing.Size(1058, 430);
            this.splitContainer1.SplitterDistance = 36;
            this.splitContainer1.TabIndex = 1;
            // 
            // btnWordAdd
            // 
            this.btnWordAdd.Location = new System.Drawing.Point(582, 7);
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
            this.label2.Location = new System.Drawing.Point(4, 12);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(57, 12);
            this.label2.TabIndex = 5;
            this.label2.Text = "검색 항목";
            // 
            // cboWordSearchItem
            // 
            this.cboWordSearchItem.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboWordSearchItem.FormattingEnabled = true;
            this.cboWordSearchItem.Location = new System.Drawing.Point(61, 8);
            this.cboWordSearchItem.Name = "cboWordSearchItem";
            this.cboWordSearchItem.Size = new System.Drawing.Size(155, 20);
            this.cboWordSearchItem.TabIndex = 4;
            // 
            // txtWordSearchWord
            // 
            this.txtWordSearchWord.Location = new System.Drawing.Point(270, 8);
            this.txtWordSearchWord.Name = "txtWordSearchWord";
            this.txtWordSearchWord.Size = new System.Drawing.Size(144, 21);
            this.txtWordSearchWord.TabIndex = 3;
            this.txtWordSearchWord.TextChanged += new System.EventHandler(this.txtWordSearchWord_TextChanged);
            this.txtWordSearchWord.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.txtWordSearchWord_KeyPress);
            // 
            // btnWordAll
            // 
            this.btnWordAll.Location = new System.Drawing.Point(493, 7);
            this.btnWordAll.Name = "btnWordAll";
            this.btnWordAll.Size = new System.Drawing.Size(75, 23);
            this.btnWordAll.TabIndex = 2;
            this.btnWordAll.Text = "전체(&A)";
            this.btnWordAll.UseVisualStyleBackColor = true;
            this.btnWordAll.Click += new System.EventHandler(this.btnWordAll_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(228, 13);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(41, 12);
            this.label1.TabIndex = 1;
            this.label1.Text = "검색어";
            // 
            // btnWordSearch
            // 
            this.btnWordSearch.Enabled = false;
            this.btnWordSearch.Location = new System.Drawing.Point(417, 7);
            this.btnWordSearch.Name = "btnWordSearch";
            this.btnWordSearch.Size = new System.Drawing.Size(75, 23);
            this.btnWordSearch.TabIndex = 0;
            this.btnWordSearch.Text = "검색(&S)";
            this.btnWordSearch.UseVisualStyleBackColor = true;
            this.btnWordSearch.Click += new System.EventHandler(this.btnWordSearch_Click);
            // 
            // dataWordGridView
            // 
            this.dataWordGridView.AllowUserToAddRows = false;
            dataGridViewCellStyle2.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(192)))));
            this.dataWordGridView.AlternatingRowsDefaultCellStyle = dataGridViewCellStyle2;
            this.dataWordGridView.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.dataWordGridView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataWordGridView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column4,
            this.Column1,
            this.Column2,
            this.Column3,
            this.Column10});
            this.dataWordGridView.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dataWordGridView.Location = new System.Drawing.Point(0, 0);
            this.dataWordGridView.MultiSelect = false;
            this.dataWordGridView.Name = "dataWordGridView";
            this.dataWordGridView.ReadOnly = true;
            this.dataWordGridView.RowTemplate.Height = 23;
            this.dataWordGridView.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataWordGridView.Size = new System.Drawing.Size(1058, 390);
            this.dataWordGridView.TabIndex = 2;
            this.dataWordGridView.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dataWordGridView_CellMouseDoubleClick);
            this.dataWordGridView.RowPostPaint += new System.Windows.Forms.DataGridViewRowPostPaintEventHandler(this.dataWordGridView_RowPostPaint);
            this.dataWordGridView.UserDeletedRow += new System.Windows.Forms.DataGridViewRowEventHandler(this.dataWordGridView_UserDeletedRow);
            this.dataWordGridView.UserDeletingRow += new System.Windows.Forms.DataGridViewRowCancelEventHandler(this.dataWordGridView_UserDeletingRow);
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
            // Column10
            // 
            this.Column10.HeaderText = "예문";
            this.Column10.Name = "Column10";
            this.Column10.ReadOnly = true;
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tbpWord);
            this.tabControl1.Controls.Add(this.tbpHanja);
            this.tabControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControl1.Location = new System.Drawing.Point(0, 0);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(1072, 462);
            this.tabControl1.TabIndex = 0;
            // 
            // frmMain
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1072, 462);
            this.Controls.Add(this.tabControl1);
            this.Name = "frmMain";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "일본어 단어 관리";
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
            ((System.ComponentModel.ISupportInitialize)(this.dataWordGridView)).EndInit();
            this.tabControl1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TabPage tbpHanja;
        private System.Windows.Forms.TabPage tbpWord;
        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.Button btnWordAdd;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.ComboBox cboWordSearchItem;
        private System.Windows.Forms.TextBox txtWordSearchWord;
        private System.Windows.Forms.Button btnWordAll;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button btnWordSearch;
        private System.Windows.Forms.DataGridView dataWordGridView;
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
        private System.Windows.Forms.DataGridViewTextBoxColumn Column9;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column5;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column6;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column7;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column8;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column11;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column10;

    }
}

