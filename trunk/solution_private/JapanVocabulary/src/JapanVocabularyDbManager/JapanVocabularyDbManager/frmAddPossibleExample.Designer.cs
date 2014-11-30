namespace JapanVocabularyDbManager
{
    partial class frmAddPossibleExample
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
            this.dataExampleGridView = new System.Windows.Forms.DataGridView();
            this.btnOK = new System.Windows.Forms.Button();
            this.btnCancel = new System.Windows.Forms.Button();
            this.webBrowser = new System.Windows.Forms.WebBrowser();
            this.Column3 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
            this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            ((System.ComponentModel.ISupportInitialize)(this.dataExampleGridView)).BeginInit();
            this.SuspendLayout();
            // 
            // dataExampleGridView
            // 
            this.dataExampleGridView.AllowUserToAddRows = false;
            this.dataExampleGridView.AllowUserToDeleteRows = false;
            this.dataExampleGridView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataExampleGridView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column3,
            this.Column4,
            this.Column1,
            this.Column2});
            this.dataExampleGridView.Location = new System.Drawing.Point(12, 12);
            this.dataExampleGridView.MultiSelect = false;
            this.dataExampleGridView.Name = "dataExampleGridView";
            this.dataExampleGridView.ReadOnly = true;
            this.dataExampleGridView.RowTemplate.Height = 23;
            this.dataExampleGridView.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataExampleGridView.Size = new System.Drawing.Size(1191, 241);
            this.dataExampleGridView.TabIndex = 6;
            this.dataExampleGridView.SelectionChanged += new System.EventHandler(this.dataExampleGridView_SelectionChanged);
            // 
            // btnOK
            // 
            this.btnOK.Location = new System.Drawing.Point(712, 259);
            this.btnOK.Name = "btnOK";
            this.btnOK.Size = new System.Drawing.Size(75, 23);
            this.btnOK.TabIndex = 5;
            this.btnOK.Text = "추가";
            this.btnOK.UseVisualStyleBackColor = true;
            this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
            // 
            // btnCancel
            // 
            this.btnCancel.Location = new System.Drawing.Point(793, 259);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(75, 23);
            this.btnCancel.TabIndex = 4;
            this.btnCancel.Text = "취소";
            this.btnCancel.UseVisualStyleBackColor = true;
            this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
            // 
            // webBrowser
            // 
            this.webBrowser.Location = new System.Drawing.Point(12, 259);
            this.webBrowser.MinimumSize = new System.Drawing.Size(20, 20);
            this.webBrowser.Name = "webBrowser";
            this.webBrowser.Size = new System.Drawing.Size(692, 65);
            this.webBrowser.TabIndex = 7;
            // 
            // Column3
            // 
            this.Column3.HeaderText = "";
            this.Column3.Name = "Column3";
            this.Column3.ReadOnly = true;
            this.Column3.Width = 40;
            // 
            // Column4
            // 
            this.Column4.HeaderText = "idx";
            this.Column4.Name = "Column4";
            this.Column4.ReadOnly = true;
            this.Column4.Width = 40;
            // 
            // Column1
            // 
            this.Column1.HeaderText = "예문";
            this.Column1.Name = "Column1";
            this.Column1.ReadOnly = true;
            this.Column1.Width = 750;
            // 
            // Column2
            // 
            this.Column2.HeaderText = "해석";
            this.Column2.Name = "Column2";
            this.Column2.ReadOnly = true;
            this.Column2.Width = 350;
            // 
            // frmAddPossibleExample
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1217, 352);
            this.Controls.Add(this.dataExampleGridView);
            this.Controls.Add(this.btnOK);
            this.Controls.Add(this.btnCancel);
            this.Controls.Add(this.webBrowser);
            this.Name = "frmAddPossibleExample";
            this.Text = "frmAddPossibleExample";
            this.Load += new System.EventHandler(this.frmAddPossibleExample_Load);
            ((System.ComponentModel.ISupportInitialize)(this.dataExampleGridView)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.DataGridView dataExampleGridView;
        private System.Windows.Forms.Button btnOK;
        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.WebBrowser webBrowser;
        private System.Windows.Forms.DataGridViewCheckBoxColumn Column3;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
    }
}