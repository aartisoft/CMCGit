package com.clubmycab.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;

import com.clubmycab.R;
import com.clubmycab.adapter.ContactListAdapter;
import com.clubmycab.adapter.GroupListAdapter;
import com.clubmycab.model.ContactData;
import com.clubmycab.ui.GroupListDialog;
import com.clubmycab.ui.SendInvitesToOtherScreen;


public class ContactListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ListView contactslist;
    private ContactListAdapter contactListAdapter;
    private ArrayList<ContactData> contactArrayList = new ArrayList<ContactData>();
    private ArrayList<ContactData> tempArrayList = new ArrayList<ContactData>();
    private ArrayList<ContactData> groupDataList = new ArrayList<ContactData>();
    private EditText etName;
    private ProgressDialog progressDialog;
    private Dialog glDialog;

    public ContactListFragment() {
    }
    private ContactListListener contactListListener;
    public interface ContactListListener{
    	public void onContactListModified(ArrayList<ContactData> arrayList);
    }
    
    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    	contactListListener = (ContactListListener)activity;
    }

    public static ContactListFragment newInstance(Bundle args) {
        ContactListFragment fragment = new ContactListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactslist = (ListView) view.findViewById(R.id.contactslist);
        etName = (EditText) view.findViewById(R.id.etName);
        etName.addTextChangedListener(textWatcher);
        contactListAdapter = new ContactListAdapter();
        contactListAdapter.init(getActivity(), contactArrayList);
        contactslist.setAdapter(contactListAdapter);
        new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				 LoadContacts loadContacts = new LoadContacts();
			        loadContacts.execute(null,null,null);
			}
		}, 500);
      

    }

    private  void notifyAdapter(ArrayList<ContactData> arrayList) {

        try{
        	if (contactListAdapter != null) {
                contactListAdapter.init(getActivity(), arrayList);
                contactListAdapter.notifyDataSetChanged();
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    /**
     * Add User from contact list to group
     * - Remove user from main contact list
     * - Add user to group list
     * - notify list
     * - update groupCount
     */
    public void addUserToGroup(ContactData contactData) {
        contactArrayList.remove(contactData);
        tempArrayList.remove(contactData);
        groupDataList.add(contactData);
        ((SendInvitesToOtherScreen) getActivity()).updateCount(groupDataList.size());
        contactListListener.onContactListModified(groupDataList);
    }

    public void removeUserFromGroup(ContactData contactData) {
       // glDialog.dismiss();
        groupDataList.remove(contactData);
        contactArrayList.add(contactData);
        ((SendInvitesToOtherScreen) getActivity()).updateCount(groupDataList.size());
        notifyAdapter(contactArrayList);
        contactListListener.onContactListModified(groupDataList);

    }

    public void showGrouplistDialog() {
        if(glDialog != null && glDialog.isShowing()){
            return;
        }else {
            glDialog = new GroupListDialog(getActivity());
            glDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            glDialog.setContentView(R.layout.dialog_grouplist);
            glDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ListView groupList = (ListView) glDialog.findViewById(R.id.groupList);
            GroupListAdapter groupListAdapter = new GroupListAdapter();
            groupListAdapter = new GroupListAdapter();
            groupListAdapter.init(getActivity(), groupDataList);
            groupList.setAdapter(groupListAdapter);
            glDialog.show();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void searchList(String str) {
        tempArrayList.clear();
        for (ContactData d : contactArrayList) {
           // if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(d.getName(), str))
            if(d.getSearchstring().contains(str.toLowerCase()))
                tempArrayList.add(d);
        }
        notifyAdapter(tempArrayList);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 0) {
                searchList(s.toString());
            } else {

                notifyAdapter(contactArrayList);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

   


    private void showProgressDialog() {
      /*  if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(getActivity(), "Loading Contacts", "please wait...", true);*/
    	getView().findViewById(R.id.pBarContacts).setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
       /* if (progressDialog != null)
            progressDialog.dismiss();*/
    	if(getView() !=null)
    	getView().findViewById(R.id.pBarContacts).setVisibility(View.GONE);

    }

    class LoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            fetchContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressDialog();
            if(contactArrayList !=null && contactArrayList.size()>0){
                notifyAdapter(contactArrayList);
            }
        }
    }

    public synchronized void fetchContacts() {
        try {
            String phoneNumber = null;
            String email = null;
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

            //    Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            //    String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            //     String DATA = ContactsContract.CommonDataKinds.Email.DATA;


            ContentResolver contentResolver = getActivity().getContentResolver();

            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

            // Loop for every contact in the phone
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    ContactData contactData = new ContactData();
                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                    if (hasPhoneNumber >0 && name.length() > 0) {


                        // Query and loop for every phone number of the contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            if(phoneNumber.length()>=10){
                            	contactData.setPhoneNumber(phoneNumber);
                                contactData.setName(name);
                                contactData.setSearchstring(name.toLowerCase());
                                contactArrayList.add(contactData);

                            }

                        }

                        phoneCursor.close();

                    } else {
                        continue;
                    }

                }
                cursor.close();
                if (contactArrayList.size() > 0) {
                    Collections.sort(contactArrayList, new Comparator<ContactData>() {
                        @Override
                        public int compare(final ContactData object1, final ContactData object2) {
                            return object1.getName().compareTo(object2.getName());
                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }




}
