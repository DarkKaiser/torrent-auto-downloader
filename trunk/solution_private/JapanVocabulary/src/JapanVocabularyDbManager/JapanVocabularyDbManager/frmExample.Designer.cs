namespace JapanVocabularyDbManager
{
    partial class frmExample
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
            this.btnCancel = new System.Windows.Forms.Button();
            this.btnOK = new System.Windows.Forms.Button();
            this.dataExampleGridView = new System.Windows.Forms.DataGridView();
            this.webBrowser = new System.Windows.Forms.WebBrowser();
            this.Column3 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
            this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            ((System.ComponentModel.ISupportInitialize)(this.dataExampleGridView)).BeginInit();
            this.SuspendLayout();
            // 
            // btnCancel
            // 
            this.btnCancel.Location = new System.Drawing.Point(791, 301);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(75, 23);
            this.btnCancel.TabIndex = 0;
            this.btnCancel.Text = "취소";
            this.btnCancel.UseVisualStyleBackColor = true;
            this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
            // 
            // btnOK
            // 
            this.btnOK.Location = new System.Drawing.Point(710, 301);
            this.btnOK.Name = "btnOK";
            this.btnOK.Size = new System.Drawing.Size(75, 23);
            this.btnOK.TabIndex = 1;
            this.btnOK.Text = "추가";
            this.btnOK.UseVisualStyleBackColor = true;
            this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
            // 
            // dataExampleGridView
            // 
            this.dataExampleGridView.AllowUserToAddRows = false;
            this.dataExampleGridView.AllowUserToDeleteRows = false;
            this.dataExampleGridView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataExampleGridView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column3,
            this.Column1,
            this.Column2});
            this.dataExampleGridView.Location = new System.Drawing.Point(12, 12);
            this.dataExampleGridView.MultiSelect = false;
            this.dataExampleGridView.Name = "dataExampleGridView";
            this.dataExampleGridView.RowTemplate.Height = 23;
            this.dataExampleGridView.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataExampleGridView.Size = new System.Drawing.Size(1191, 241);
            this.dataExampleGridView.TabIndex = 2;
            this.dataExampleGridView.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dataExampleGridView_RowEnter);
            // 
            // webBrowser
            // 
            this.webBrowser.Location = new System.Drawing.Point(12, 259);
            this.webBrowser.MinimumSize = new System.Drawing.Size(20, 20);
            this.webBrowser.Name = "webBrowser";
            this.webBrowser.Size = new System.Drawing.Size(692, 65);
            this.webBrowser.TabIndex = 3;
            // 
            // Column3
            // 
            this.Column3.HeaderText = "";
            this.Column3.Name = "Column3";
            this.Column3.Width = 40;
            // 
            // Column1
            // 
            this.Column1.HeaderText = "예문";
            this.Column1.Name = "Column1";
            this.Column1.Width = 750;
            // 
            // Column2
            // 
            this.Column2.HeaderText = "해석";
            this.Column2.Name = "Column2";
            this.Column2.Width = 350;
            // 
            // frmExample
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1215, 335);
            this.Controls.Add(this.webBrowser);
            this.Controls.Add(this.dataExampleGridView);
            this.Controls.Add(this.btnOK);
            this.Controls.Add(this.btnCancel);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Name = "frmExample";
            this.ShowInTaskbar = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "예문";
            this.Load += new System.EventHandler(this.frmExample_Load);
            ((System.ComponentModel.ISupportInitialize)(this.dataExampleGridView)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.Button btnOK;
        private System.Windows.Forms.DataGridView dataExampleGridView;
        private System.Windows.Forms.WebBrowser webBrowser;
        private System.Windows.Forms.DataGridViewCheckBoxColumn Column3;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
    }
}