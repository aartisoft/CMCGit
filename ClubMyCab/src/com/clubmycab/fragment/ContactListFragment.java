package com.clubmycab.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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
    private ArrayList<ContactData> contatcMainList = new ArrayList<ContactData>();
    private ArrayList<ContactData> tempArrayList = new ArrayList<ContactData>();
    private ArrayList<ContactData> selectedContactList = new ArrayList<ContactData>();
    private EditText etName;
    private ProgressDialog progressDialog;
    private Dialog glDialog;
    private boolean isContactLoded;
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
        contactListAdapter.init(getActivity(), contatcMainList);
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
        contatcMainList.remove(contactData);
        tempArrayList.remove(contactData);
        selectedContactList.add(contactData);
        ((SendInvitesToOtherScreen) getActivity()).updateCount(selectedContactList.size());
        contactListListener.onContactListModified(selectedContactList);
    }

    public void removeUserFromGroup(ContactData contactData) {
       // glDialog.dismiss();
        selectedContactList.remove(contactData);
        contatcMainList.add(contactData);
        ((SendInvitesToOtherScreen) getActivity()).updateCount(selectedContactList.size());
        notifyAdapter(contatcMainList);
        contactListListener.onContactListModified(selectedContactList);
        if(selectedContactList.size() ==0){
        	glDialog.dismiss();
        }

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
            groupListAdapter.init(getActivity(), selectedContactList);
            groupList.setAdapter(groupListAdapter);
            glDialog.show();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void searchList(String str) {
        tempArrayList.clear();
        for (ContactData d : contatcMainList) {
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
        //	if(isContactLoded){
        		if (count > 0) {
                    searchList(s.toString());
                } else {

                    notifyAdapter(contatcMainList);
                }
        //	}
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
            isContactLoded = false;
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
            if(contatcMainList !=null && contatcMainList.size()>0){
            	isContactLoded = true;
                notifyAdapter(contatcMainList);
            }
        }
    }

 
    
    private void fetchContacts(){
    	Cursor cursor = null;
		try {
			cursor = getActivity().getContentResolver().query(
					Phone.CONTENT_URI, null, null, null, null);
			int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
			int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
			int imageIdx = cursor.getColumnIndex(Phone.CONTACT_ID);

			cursor.moveToFirst();
			HashSet set = new HashSet();

			do {
				
				if (cursor.getString(phoneNumberIdx).length() > 10) {
					ContactData contactData = new ContactData();
					contactData.setName(cursor.getString(nameIdx));
					contactData.setSearchstring(cursor.getString(nameIdx).toLowerCase());
					String phonenumbercapt = cursor.getString(phoneNumberIdx);

					String phoneStr = phonenumbercapt.replaceAll("\\D+", "");

					String phonesub = null;
					if (phoneStr.length() > 10) {
						phonesub = phoneStr.substring(phoneStr.length() - 10,
								phoneStr.length());
					} else {
						phonesub = phoneStr;
					}

					contactData.setPhoneNumber(phonesub);
					boolean result = set.add(phonesub);
					if(result){
						contatcMainList.add(contactData);
					}
					//imagearray.add(cursor.getString(imageIdx));
				}

			} while (cursor.moveToNext());
			set.clear();
			if (contatcMainList.size() > 0) {
                Collections.sort(contatcMainList, new Comparator<ContactData>() {
                    @Override
                    public int compare(final ContactData object1, final ContactData object2) {
                        return object1.getName().compareTo(object2.getName());
                    }
                });

            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		/*HashSet set = new HashSet();
		for (int i = 0; i < phonenoarray.size(); i++) {
			boolean val = set.add(phonenoarray.get(i));
			if (val == false) {

			} else {
				namearraynew.add(namearray.get(i));
				phonenoarraynew.add(phonenoarray.get(i));
				imagearraynew.add(imagearray.get(i));
			}
		}*/
    }

	public void clearSectedContacts() {
		contatcMainList.addAll(selectedContactList);
		selectedContactList.clear();
		contactListListener.onContactListModified(selectedContactList);
		if (contatcMainList.size() > 0) {
            Collections.sort(contatcMainList, new Comparator<ContactData>() {
                @Override
                public int compare(final ContactData object1, final ContactData object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });

        }
		notifyAdapter(contatcMainList);
		
	}




}
