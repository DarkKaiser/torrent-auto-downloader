package kr.co.darkkaiser.jv.view.list;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import kr.co.darkkaiser.jv.data.JapanVocabulary;
import kr.co.darkkaiser.jv.data.JapanVocabularyComparator;
import kr.co.darkkaiser.jv.JvDefines;
import kr.co.darkkaiser.jv.data.JapanVocabularyManager;
import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.view.detail.JvDetailActivity;
import kr.co.darkkaiser.jv.view.option.OptionActivity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class JvListActivity extends ListActivity implements OnClickListener {

	// ȣ���� ����Ʈ�� �Ѱ� �� ��Ƽ��Ƽ ��� ��, �� ������ ���� ��Ÿ���̾�� ��.
	public static final int ACTIVITY_RESULT_DATA_CHANGED = 1;
	public static final int ACTIVITY_RESULT_PREFERENCE_CHANGED = 2;

	public static final int MSG_CHANGED_LIST_DATA = 1;
	public static final int MSG_COMPLETED_LIST_DATA_UPDATE = 2;

	private ProgressDialog mProgressDialog = null;
	private SharedPreferences mPreferences = null;

	private JvListAdapter mJvListAdapter = null;
	private ArrayList<JapanVocabulary> mJvListData = null;
	private JvListSortMethod mJvListSortMethod = JvListSortMethod.REGISTRATION_DATE_DOWN;

	private Thread mJvListSearchThread = null;
	private JvListSearchCondition mJvListSearchCondition = null;

	private int mActivityResultCode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jv_list);

		// ���ؽ�Ʈ �޴��� ����Ѵ�.
		registerForContextMenu(getListView());
		
		// Ÿ��Ʋ�� �����Ѵ�.
		setTitle(String.format("%s - �ܾ�˻�", getResources().getString(R.string.app_name)));

		// ������ ������ �� ȯ�漳�� ������ �о���δ�.
		mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		mJvListSortMethod = JvListSortMethod.valueOf(mPreferences.getString(JvDefines.JV_SPN_LIST_SORT_METHOD, JvListSortMethod.REGISTRATION_DATE_DOWN.name()));
		mJvListSearchCondition = new JvListSearchCondition(this, mPreferences);

		// �ܾ� ����Ʈ�� �ʱ�ȭ�Ѵ�.
		mJvListData = new ArrayList<JapanVocabulary>();
		mJvListAdapter = new JvListAdapter(this, R.layout.jv_listitem, mJvListDataChangedHandler, mJvListData);
		setListAdapter(mJvListAdapter);

		//
		// �˻��� ���õ� ��Ʈ�ѵ��� �ʱ�ȭ�մϴ�.
		//

		// �˻��� �˻� ����
		EditText scSearchWordEditText = (EditText)findViewById(R.id.sc_search_word);
		scSearchWordEditText.setText(mJvListSearchCondition.getSearchWord());

		// �ϱ� ��� �˻� ����
		ArrayAdapter<String> scMemorizeTargetAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sc_memorize_target));
		scMemorizeTargetAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

		Spinner scMemorizeTargetSpinner = (Spinner)findViewById(R.id.sc_memorize_target);
		scMemorizeTargetSpinner.setAdapter(scMemorizeTargetAdapter);
		scMemorizeTargetSpinner.setPrompt("�˻� ����");
		scMemorizeTargetSpinner.setSelection(mJvListSearchCondition.getMemorizeTargetPosition());

		// �ϱ� �Ϸ� �˻� ����
		ArrayAdapter<String> scMemorizeCompletedAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sc_memorize_completed));
		scMemorizeCompletedAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

		Spinner scMemorizeCompletedSpinner = (Spinner)findViewById(R.id.sc_memorize_completed);
		scMemorizeCompletedSpinner.setAdapter(scMemorizeCompletedAdapter);
		scMemorizeCompletedSpinner.setPrompt("�˻� ����");
		scMemorizeCompletedSpinner.setSelection(mJvListSearchCondition.getMemorizeCompletedPosition());

		// �ܾ� ����� �˻� ����
		Button btnLastSearchDate = (Button)findViewById(R.id.sc_last_search_date);
		Button btnFirstSearchDate = (Button)findViewById(R.id.sc_first_search_date);
		CheckBox cboAllRegDateSearch = (CheckBox)findViewById(R.id.sc_all_reg_date_search);

		if (mJvListSearchCondition.isAllRegDateSearch() == true) {
			cboAllRegDateSearch.setChecked(true);

			Calendar currentDate = Calendar.getInstance();
			btnLastSearchDate.setText(String.format("%04d/%02d/%02d",
					currentDate.get(Calendar.YEAR),
					currentDate.get(Calendar.MONTH) + 1,
					currentDate.get(Calendar.DATE)));
			
			currentDate.add(Calendar.DAY_OF_MONTH, -7);
			btnFirstSearchDate.setText(String.format("%04d/%02d/%02d",
					currentDate.get(Calendar.YEAR), 
					currentDate.get(Calendar.MONTH) + 1, 
					currentDate.get(Calendar.DATE)));
		} else {
			cboAllRegDateSearch.setChecked(false);

			btnFirstSearchDate.setText(mJvListSearchCondition.getFirstSearchDate());
			btnLastSearchDate.setText(mJvListSearchCondition.getLastSearchDate());

			btnLastSearchDate.setEnabled(true);
			btnFirstSearchDate.setEnabled(true);
		}

		btnLastSearchDate.setOnClickListener(this);
		btnFirstSearchDate.setOnClickListener(this);
		cboAllRegDateSearch.setOnClickListener(this);

		// JLPT �޼� �˻� ����
		updateJLPTLevelButtonText();
		findViewById(R.id.sc_jlpt_level).setOnClickListener(this);

		// ��Ÿ
		findViewById(R.id.search_start).setOnClickListener(this);
		findViewById(R.id.search_cancel).setOnClickListener(this);

		//
		// �ֱ��� �˻� ������ �̿��Ͽ� �˻��� �����Ѵ�.
		//
		searchVocabulary();
	}

	private void updateJLPTLevelButtonText() {
		String[] items = getResources().getStringArray(R.array.sc_jlpt_simple_level_list);
		boolean[] checkedItems = mJvListSearchCondition.getCheckedJLPTLevelArray();
		assert items.length == checkedItems.length;

		StringBuilder sb = new StringBuilder();
		for (int index = 0; index < checkedItems.length; ++index) {
			if (checkedItems[index] == true) {
				if (sb.length() > 0)
					sb.append(", ");

				sb.append(items[index]);
			}
		}
		
		if (sb.length() == 0)
			sb.append("��ü �˻�\n<���� �׸� ����>");

		Button scJLPTLevelButton = (Button)findViewById(R.id.sc_jlpt_level);
		scJLPTLevelButton.setText(sb.toString());
	}

	@Override
	public void onBackPressed() {
		SlidingDrawer searchSlidingDrawer = (SlidingDrawer) findViewById(R.id.search_sliding_drawer);
		if (searchSlidingDrawer.isOpened() == true) {
			searchSlidingDrawer.animateClose();
			return;
		}

		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.jv_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.jvlm_sort_vocabulary:
			startSortList(JvListSortMethod.VOCABULARY);
			return true;
		case R.id.jvlm_sort_vocabulary_gana:
			startSortList(JvListSortMethod.VOCABULARY_GANA);
			return true;
		case R.id.jvlm_sort_vocabulary_translation:
			startSortList(JvListSortMethod.VOCABULARY_TRANSLATION);
			return true;
		case R.id.jvlm_sort_registration_date_up:
			startSortList(JvListSortMethod.REGISTRATION_DATE_UP);
			return true;
		case R.id.jvlm_sort_registration_date_down:
			startSortList(JvListSortMethod.REGISTRATION_DATE_DOWN);
			return true;
		case R.id.jvlm_all_rememorize: 				// �˻��� ��ü �ܾ� ��ϱ�
		case R.id.jvlm_all_memorize_completed: 		// �˻��� ��ü �ܾ� �ϱ� �Ϸ�
		case R.id.jvlm_all_memorize_target: 		// �˻��� ��ü �ܾ� �ϱ� ��� �����
		case R.id.jvlm_all_memorize_target_cancel: 	// �˻��� ��ü �ܾ� �ϱ� ��� ����
			// ȣ���� ��Ƽ��Ƽ���� �����Ͱ� ����Ǿ����� �˸����� ���� �����Ѵ�.
			mActivityResultCode |= ACTIVITY_RESULT_DATA_CHANGED;
			setResult(mActivityResultCode);

			final int itemId = item.getItemId();
			if (item.getItemId() == R.id.jvlm_all_memorize_target) {
				new AlertDialog.Builder(this)
					.setTitle("�ϱ� ��� ���·� �����")
					.setMessage("�˻� ����� ���Ե��� ���� �ܾ���� �ϱ� ��� ���¸� �����Ͻðڽ��ϱ�?\n\n(��)�� �����ø� �˻��� �ܾ�� �ϱ� ��� ���·�, �˻� ����� ���Ե��� ���� �ܾ���� �ϱ� ��� ���¸� �����մϴ�.\n\n(�ƴϿ�)�� �����ø� �˻��� �ܾ �ϱ� ��� ���·� ����ϴ�.")
					.setPositiveButton("��", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							memorizeSetupVocabulary(itemId, true);
						}
					})
					.setNegativeButton("�ƴϿ�", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							memorizeSetupVocabulary(itemId, false);
						}
					})
					.show();
			} else {
				memorizeSetupVocabulary(itemId, false);
			}

			return true;

		case R.id.jvm_preferences:
			// ���� �������� ����.
			startActivityForResult(new Intent(this, OptionActivity.class), R.id.jvm_preferences);
			mActivityResultCode |= ACTIVITY_RESULT_PREFERENCE_CHANGED;
			setResult(mActivityResultCode);

			return true;
		}

		return false;
	}
	
	private void memorizeSetupVocabulary(int menuId, boolean notSearchVocabularyTargetCancel) {
		// �����͸� ó���ϴ� ���߿� ���α׷��� ��ȭ���ڸ� ���δ�.
		mProgressDialog = ProgressDialog.show(this, null, "��û�Ͻ� �۾��� ó�� ���Դϴ�.", true, false);

		new Thread() {

			private int mMenuItemId;
			private boolean mNotSearchVocabularyTargetCancel = false;

			public Thread setValues(int menuItemId, boolean notSearchVocabularyTargetCancel) {
				mMenuItemId = menuItemId;
				mNotSearchVocabularyTargetCancel = notSearchVocabularyTargetCancel;
				return this;
			}
			
			@Override
			public void run() {
				JapanVocabulary jpVocabulary = null;
				ArrayList<Long> idxList = new ArrayList<Long>();

				synchronized (mJvListData) {
					if (mMenuItemId == R.id.jvlm_all_rememorize) { 						// �˻��� ��ü �ܾ� ��ϱ�
						for (int index = 0; index < mJvListData.size(); ++index) {
							jpVocabulary = mJvListData.get(index);
							if (jpVocabulary.isMemorizeTarget() == false || jpVocabulary.isMemorizeCompleted() == true)
								idxList.add(jpVocabulary.getIdx());
						}
					} else if (mMenuItemId == R.id.jvlm_all_memorize_completed) { 		// �˻��� ��ü �ܾ� �ϱ� �Ϸ�
						for (int index = 0; index < mJvListData.size(); ++index) {
							jpVocabulary = mJvListData.get(index);
							if (jpVocabulary.isMemorizeCompleted() == false)
								idxList.add(jpVocabulary.getIdx());
						}
					} else if (mMenuItemId == R.id.jvlm_all_memorize_target) { 			// �˻��� ��ü �ܾ� �ϱ� ��� �����
						if (mNotSearchVocabularyTargetCancel == true) {
							for (int index = 0; index < mJvListData.size(); ++index) {
								idxList.add(mJvListData.get(index).getIdx());
							}							
						} else {
							for (int index = 0; index < mJvListData.size(); ++index) {
								jpVocabulary = mJvListData.get(index);
								if (jpVocabulary.isMemorizeTarget() == false)
									idxList.add(jpVocabulary.getIdx());
							}							
						}
					} else if (mMenuItemId == R.id.jvlm_all_memorize_target_cancel) { 	// �˻��� ��ü �ܾ� �ϱ� ��� ����
						for (int index = 0; index < mJvListData.size(); ++index) {
							jpVocabulary = mJvListData.get(index);
							if (jpVocabulary.isMemorizeTarget() == true)
								idxList.add(jpVocabulary.getIdx());
						}
					}

					JapanVocabularyManager.getInstance().updateMemorizeField(mMenuItemId, mNotSearchVocabularyTargetCancel, idxList);					
				}

				Message msg = Message.obtain();
				msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
				mJvListDataChangedHandler.sendMessage(msg);
			};
		}
		.setValues(menuId, notSearchVocabularyTargetCancel)
		.start();		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.id.jvm_preferences) {
			// ���� �۾� ����
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startSortList(JvListSortMethod jvListSortMethod) {
		if (mJvListSortMethod == jvListSortMethod)
			return;

		mJvListSortMethod = jvListSortMethod;

		// �����߿� ���α׷��� ��ȭ���ڸ� ���δ�.
		assert mProgressDialog == null;
		mProgressDialog = ProgressDialog.show(this, null, "��ü ����Ʈ�� �������Դϴ�.", true, false);

		new Thread() {
			@Override
			public void run() {
				// ����� ���� ����� �����Ѵ�.
				mPreferences.edit().putString(JvDefines.JV_SPN_LIST_SORT_METHOD, mJvListSortMethod.name()).commit();

				// ����Ʈ ������ �����մϴ�.
				sortList();

				Message msg = Message.obtain();
				msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
				mJvListDataChangedHandler.sendMessage(msg);
			};
		}.start();
	}

	private void sortList() {
		synchronized (mJvListData) {
			switch (mJvListSortMethod) {
			case VOCABULARY:
				Collections.sort(mJvListData, JapanVocabularyComparator.mJvVocabularyComparator);
				break;
			case VOCABULARY_GANA:
				Collections.sort(mJvListData, JapanVocabularyComparator.mJvVocabularyGanaComparator);
				break;
			case VOCABULARY_TRANSLATION:
				Collections.sort(mJvListData, JapanVocabularyComparator.mJvVocabularyTranslationComparator);
				break;
			case REGISTRATION_DATE_UP:
				Collections.sort(mJvListData, JapanVocabularyComparator.mJvRegistrationDateUpComparator);
				break;
			case REGISTRATION_DATE_DOWN:
				Collections.sort(mJvListData, JapanVocabularyComparator.mJvRegistrationDateDownComparator);
				break;
			}			
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		synchronized (mJvListData) {
			// �ܾ� �������� ȣ��
			Intent intent = new Intent(this, JvDetailActivity.class);
			intent.putExtra("idx", mJvListData.get(position).getIdx());
			startActivity(intent);			
		}
	}

	private Handler mJvListDataChangedHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_COMPLETED_LIST_DATA_UPDATE) {
				mJvListAdapter.notifyDataSetChanged();
				updateVocabularyInfo();

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				mProgressDialog = null;
				mJvListSearchThread = null;
			} else if (msg.what == MSG_CHANGED_LIST_DATA) {
				mJvListAdapter.notifyDataSetChanged();
				updateVocabularyInfo();

				// ȣ���� ��Ƽ��Ƽ���� �����Ͱ� ����Ǿ����� �˸����� �Ѵ�.
				mActivityResultCode |= ACTIVITY_RESULT_DATA_CHANGED;
				setResult(mActivityResultCode);
			}
		};
	};

	private void searchVocabulary() {
		// �ܾ� �˻��� ���������� ���� ��ȭ���ڸ� ���δ�.
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(this, null, "�ܾ �˻� ���Դϴ�.", true, true);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					if (mJvListSearchThread != null) {
						mJvListSearchThread.interrupt();
					}

					synchronized (mJvListData) {
						// �˻��� ����Ͽ����Ƿ� �ܾ ��� �����Ѵ�.
						mJvListData.clear();						
					}

					Toast.makeText(JvListActivity.this, "�ܾ� �˻��� ��ҵǾ����ϴ�", Toast.LENGTH_SHORT).show();

					Message msg = Message.obtain();
					msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
					mJvListDataChangedHandler.sendMessage(msg);
				}
			});
		}

		mJvListSearchThread = new JvListSearchThread(mJvListSearchCondition);
		mJvListSearchThread.start();
	}

	public class JvListSearchThread extends Thread {

		private JvListSearchCondition mJvListSearchCondition = null;

		public JvListSearchThread(JvListSearchCondition jvListSearchCondition) {
			assert jvListSearchCondition != null;
			mJvListSearchCondition = jvListSearchCondition;
		}

		@Override
		public void run() {
			// ���� �˻��Ǵ� �˻� ������ ������ ���´�.
			mJvListSearchCondition.commit();

			synchronized (mJvListData) {
				mJvListData.clear();
				JapanVocabularyManager.getInstance().searchVocabulary(JvListActivity.this, mJvListSearchCondition, mJvListData);
			}

			sortList();

			Message msg = Message.obtain();
			msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
			mJvListDataChangedHandler.sendMessage(msg);
		};

	}

	private void updateVocabularyInfo() {
		ArrayList<Integer> vocabularyInfo = JapanVocabularyManager.getInstance().getVocabularyInfo();
		assert vocabularyInfo.size() == 3;

		TextView allVocabularyCount = (TextView)findViewById(R.id.all_vocabulary_count);
		TextView searchVocabularyCount = (TextView)findViewById(R.id.search_vocabulary_count);
		TextView memorizeTargetCount = (TextView)findViewById(R.id.memorize_target_count);
		TextView memorizeCompletedCount = (TextView)findViewById(R.id.memorize_completed_count);

		allVocabularyCount.setText(String.format("%d��", vocabularyInfo.get(0)));
		searchVocabularyCount.setText(String.format("%d��", mJvListData.size()));
		memorizeTargetCount.setText(String.format("%d��", vocabularyInfo.get(1)));
		memorizeCompletedCount.setText(String.format("%d��", vocabularyInfo.get(2)));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sc_jlpt_level:
		{
			boolean[] checkedItems = mJvListSearchCondition.getCheckedJLPTLevelArray();
			new AlertDialog.Builder(JvListActivity.this)
					.setTitle("�˻� ����")
					.setMultiChoiceItems(R.array.sc_jlpt_level_list, checkedItems, new OnMultiChoiceClickListener() {
						
								@Override
								public void onClick(DialogInterface dialog, int item, boolean isChecked) {
									mJvListSearchCondition.setCheckedJLPTLevel(item, isChecked);
								}
							})
					.setPositiveButton("Ȯ��",
							new DialogInterface.OnClickListener() {
						
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// ����� ����(ȭ�� ����)�� ���� �Ʒ� commit() �ϴ� �κ��� �ּ�ó���Ѵ�.
									// mJvListSearchCondition.commit();
									updateJLPTLevelButtonText();
								}
							})
					.show();
		}
			break;

		case R.id.sc_first_search_date:
		{
			Button btnSearchDateFirst = (Button)v;
			String searchDateString = btnSearchDateFirst.getText().toString().replace("/", "");
			new DatePickerDialog(
					this,
					new DatePickerDialog.OnDateSetListener() {
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
							Button searchDateFirst = (Button)findViewById(R.id.sc_first_search_date);
							searchDateFirst.setText(String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));

							// �˻� �����Ϻ��� �ֱ� ��¥�̸� �˻� �����ϵ� �����Ѵ�.
							Calendar calSearchDateFirst = Calendar.getInstance();
							calSearchDateFirst.set(year, monthOfYear, dayOfMonth);

							Button searchDateLast = (Button)findViewById(R.id.sc_last_search_date);
							String searchDateLastString = searchDateLast.getText().toString().replace("/", "");

							Calendar calSearchDateLast = Calendar.getInstance();
							calSearchDateLast.set(Integer.parseInt(searchDateLastString.substring(0, 4)),
									Integer.parseInt(searchDateLastString.substring(4, 6)) - 1,
									Integer.parseInt(searchDateLastString.substring(6, 8)));

							if (calSearchDateFirst.after(calSearchDateLast) == true)
								searchDateLast.setText(searchDateFirst.getText());
						}
					},
					Integer.parseInt(searchDateString.substring(0, 4)),
					Integer.parseInt(searchDateString.substring(4, 6)) - 1,
					Integer.parseInt(searchDateString.substring(6, 8))).show();
		}
			break;

		case R.id.sc_last_search_date:
		{
			Button btnSearchDateLast = (Button)v;
			String searchDateString = btnSearchDateLast.getText().toString().replace("/", "");
			new DatePickerDialog(
					JvListActivity.this,
					new DatePickerDialog.OnDateSetListener() {
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
							Button searchDateLast = (Button)findViewById(R.id.sc_last_search_date);
							searchDateLast.setText(String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));

							// �˻� �����Ϻ��� ���� ��¥�̸� �˻� �����ϵ� �����Ѵ�.
							Calendar calSearchDateLast = Calendar.getInstance();
							calSearchDateLast.set(year, monthOfYear, dayOfMonth);

							Button searchDateFirst = (Button)findViewById(R.id.sc_first_search_date);
							String searchDateFirstString = searchDateFirst.getText().toString().replace("/", "");

							Calendar calSearchDateFirst = Calendar.getInstance();
							calSearchDateFirst.set(Integer.parseInt(searchDateFirstString.substring(0, 4)),
									Integer.parseInt(searchDateFirstString.substring(4, 6)) - 1,
									Integer.parseInt(searchDateFirstString.substring(6, 8)));

							if (calSearchDateLast.before(calSearchDateFirst) == true)
								searchDateFirst.setText(searchDateLast.getText());
						}
					},
					Integer.parseInt(searchDateString.substring(0, 4)),
					Integer.parseInt(searchDateString.substring(4, 6)) - 1,
					Integer.parseInt(searchDateString.substring(6, 8))).show();
		}
			break;

		case R.id.sc_all_reg_date_search:
		{
			CheckBox cboAllRegDateSearch = (CheckBox)v;
			Button btnSearchDateFirst = (Button) findViewById(R.id.sc_first_search_date);
			Button btnSearchDateLast = (Button) findViewById(R.id.sc_last_search_date);
			btnSearchDateFirst.setEnabled(!cboAllRegDateSearch.isChecked());
			btnSearchDateLast.setEnabled(!cboAllRegDateSearch.isChecked());
		}
			break;

		case R.id.search_start:
		{
			// ����Ʈ Ű���尡 ��Ÿ�� �ִٸ� �����.
		    InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        EditText searchWord = (EditText)findViewById(R.id.sc_search_word);
	        mgr.hideSoftInputFromWindow(searchWord.getWindowToken(), 0);

			SlidingDrawer searchSlidingDrawer = (SlidingDrawer)findViewById(R.id.search_sliding_drawer);
			searchSlidingDrawer.animateClose();

			// ������ �˻� ���ǵ��� �����մϴ�.
			EditText scSearchWordEditText = (EditText)findViewById(R.id.sc_search_word);
			Spinner scMemorizeTargetSpinner = (Spinner)findViewById(R.id.sc_memorize_target);
			Spinner scMemorizeCompletedSpinner = (Spinner)findViewById(R.id.sc_memorize_completed);
			CheckBox scAllRegDateSearchCheckBox = (CheckBox)findViewById(R.id.sc_all_reg_date_search);
			Button scSearchDateFirstButton = (Button)findViewById(R.id.sc_first_search_date);
			Button scSearchDateLastButton = (Button)findViewById(R.id.sc_last_search_date);

			mJvListSearchCondition.setSearchWord(scSearchWordEditText.getText().toString().trim());
			mJvListSearchCondition.setMemorizeTargetPosition(scMemorizeTargetSpinner.getSelectedItemPosition());
			mJvListSearchCondition.setMemorizeCompletedPosition(scMemorizeCompletedSpinner.getSelectedItemPosition());
			mJvListSearchCondition.setAllRegDateSearch(scAllRegDateSearchCheckBox.isChecked());
			mJvListSearchCondition.setSearchDateRange(scSearchDateFirstButton.getText().toString(), scSearchDateLastButton.getText().toString());
			
			// ����� ����(ȭ�� ����)�� ���� �Ʒ� commit() �ϴ� �κ��� �ּ�ó���Ѵ�.
			// ��� commit()�� �˻��� �����ϱ� ���� �ϵ��� �����Ѵ�.
			// mJvListSearchCondition.commit();

			// ������ �˻� ������ �̿��Ͽ� �ܾ �˻��մϴ�.
			searchVocabulary();
		}
			break;

		case R.id.search_cancel:
			// ����Ʈ Ű���尡 ��Ÿ�� �ִٸ� �����.
		    InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        EditText searchWord = (EditText)findViewById(R.id.sc_search_word);
	        mgr.hideSoftInputFromWindow(searchWord.getWindowToken(), 0);

	        SlidingDrawer searchSlidingDrawer = (SlidingDrawer)findViewById(R.id.search_sliding_drawer);
			searchSlidingDrawer.animateClose();

			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.jv_list_context_menu, menu);
		menu.setHeaderTitle("�۾�");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.jvlm_remove_vocabulary:
			AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
			mJvListData.remove(menuInfo.position);
			
			updateVocabularyInfo();
			mJvListAdapter.notifyDataSetChanged();
			break;
		}

		return super.onContextItemSelected(item);
	}

}
