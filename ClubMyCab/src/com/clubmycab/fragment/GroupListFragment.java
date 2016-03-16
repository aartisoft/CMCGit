package com.clubmycab.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.clubmycab.ClubListClass;
import com.clubmycab.ClubObject;
import com.clubmycab.ClubsAdaptorNew;
import com.clubmycab.R;
import com.clubmycab.adapter.UserGroupAdapter;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.model.MemberModel;
import com.clubmycab.model.OwnerModel;
import com.clubmycab.model.UserModel;
import com.clubmycab.ui.ContactsInviteForRideActivityNew;
import com.clubmycab.ui.SplashActivity;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.StringTags;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.google.gson.JsonArray;


public class GroupListFragment extends Fragment implements
GlobalAsyncTask.AsyncTaskResultListener {
	private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView listMyclubs;
	private UserGroupAdapter adapterClubMy;
	private int flag;
	private static final String POOL_OWNER = "1";
	private ArrayList<OwnerModel> groupList = new ArrayList<OwnerModel>();
	private ArrayList<OwnerModel> selectedGroupList = new ArrayList<OwnerModel>();

    public GroupListFragment() {
    }

    public static GroupListFragment newInstance(Bundle args) {
        GroupListFragment fragment = new GroupListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    private GroplistFragmnetListener gpListener;
    public interface GroplistFragmnetListener{
    	public void onEmptyGroup();
    	public void onGroupSelected(ArrayList<OwnerModel> groupList);
    }
    
    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    	try{
    		gpListener = (GroplistFragmnetListener)activity;
    	}catch(ClassCastException e){
    		e.printStackTrace();
    	}
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
        return inflater.inflate(R.layout.fragment_group_list, container, false);
    }
    
    @Override
    	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    		// TODO Auto-generated method stub
    		super.onViewCreated(view, savedInstanceState);
    		fetchGropList();
    		listMyclubs = (ListView) view.findViewById(R.id.listMyclubs);

    		adapterClubMy = new UserGroupAdapter();
    		adapterClubMy.init(
    				getActivity(), groupList,
    				true);
    		listMyclubs.setAdapter(adapterClubMy);
    		listMyclubs.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> parent, View v,
    					int position, long id) {
    				// TODO Auto-generated method stub

    			/*	ClubObject bean = ClubListClass.ClubList.get(position);
    				bean.setSelected(true);

    				int count = Integer.parseInt(bean.getNoofMembers());

    				if (count <= 1) {

    					Toast.makeText(getActivity(),
    							StringTags.TAG_DOES_NOT_HAVE_MEMBER,
    							Toast.LENGTH_SHORT).show();
    				} else {
    					adapterClubMy.notifyDataSetChanged();
    				}*/

    			}
    		});
    	}

    private void notifyAdapter(){
    	if(adapterClubMy != null){
    		adapterClubMy.init(
    				getActivity(), groupList,
    				true);
    		adapterClubMy.notifyDataSetChanged();
    	}
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /******************
     * Fetch Group list------------------------>
     */
    
	private void fetchGropList(){
		SharedPreferences mPrefs = getActivity().getSharedPreferences("FacebookData", 0);

		String MobileNumber = mPrefs.getString("MobileNumber", "");

		String endpoint = GlobalVariables.ServiceUrl
				+ "/Fetch_Club.php";
		String authString = MobileNumber;
		String params = "OwnerNumber=" + MobileNumber + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		getView().findViewById(R.id.pBarGroups).setVisibility(View.VISIBLE);
		new GlobalAsyncTask(getActivity(), endpoint, params,
				new FetchUnreadNotificationCountHandler(), this, false,"FetchUnreadNotificationCount", false);
		

	}

	@Override
	public void getResult(String response, String uniqueID) {
		getView().findViewById(R.id.pBarGroups).setVisibility(View.GONE);
		parseGroupData(response);
		
	}
	
	/*public void eralierMethod(String response, String uniqueID) {
		try {
			getView().findViewById(R.id.pBarGroups).setVisibility(View.GONE);

			if (response.equalsIgnoreCase("No Users of your Club")) {

				flag = 1;
				gpListener.onEmptyGroup();
			}else{


		
				ClubListClass.ClubList.clear();
				ArrayList<String> MyClubPoolId = new ArrayList<String>();
				ArrayList<String> MyClubPoolName = new ArrayList<String>();
				ArrayList<String> MyClubNoofMembers = new ArrayList<String>();
				ArrayList<String> MyClubOwnerName = new ArrayList<String>();
				ArrayList<String> MyClubMembers = new ArrayList<String>();

				ArrayList<String> MemberClubPoolId = new ArrayList<String>();
				ArrayList<String> MemberClubPoolName = new ArrayList<String>();
				ArrayList<String> MemberClubNoofMembers = new ArrayList<String>();
				ArrayList<String> MemberClubOwnerName = new ArrayList<String>();
				ArrayList<String> MemberClubMembers = new ArrayList<String>();

				JSONArray subArray = new JSONArray(response);

				for (int i = 0; i < subArray.length(); i++) {

					if (subArray.getJSONObject(i).getString("IsPoolOwner")
							.toString().trim().equalsIgnoreCase("1")) {
						MyClubPoolId.add(subArray.getJSONObject(i)
								.getString("PoolId").toString());
						MyClubPoolName.add(subArray.getJSONObject(i)
								.getString("PoolName").toString());

						// Pawan cheks for null
						if (subArray.getJSONObject(i).getString("NoofMembers")
								.toString().equalsIgnoreCase("null"))
							MyClubNoofMembers.add("1");

						else
							MyClubNoofMembers.add(subArray.getJSONObject(i)
									.getString("NoofMembers").toString());

						MyClubOwnerName.add(subArray.getJSONObject(i)
								.getString("OwnerName").toString());
						MyClubMembers.add(subArray.getJSONObject(i)
								.getString("Members").toString());
					} else {
						MemberClubPoolId.add(subArray.getJSONObject(i)
								.getString("PoolId").toString());
						MemberClubPoolName.add(subArray.getJSONObject(i)
								.getString("PoolName").toString());

						if (subArray.getJSONObject(i).getString("NoofMembers")
								.toString().equalsIgnoreCase("null"))
							MemberClubNoofMembers.add("1");

						else
							MemberClubNoofMembers.add(subArray.getJSONObject(i)
									.getString("NoofMembers").toString());

						MemberClubOwnerName.add(subArray.getJSONObject(i)
								.getString("OwnerName").toString());
						MemberClubMembers.add(subArray.getJSONObject(i)
								.getString("Members").toString());
					}
				}

				Log.d("MyClubPoolId", "" + MyClubPoolId);
				Log.d("MyClubPoolName", "" + MyClubPoolName);
				Log.d("MyClubNoofMembers", "" + MyClubNoofMembers);
				Log.d("MyClubOwnerName", "" + MyClubOwnerName);
				Log.d("MyClubMembers", "" + MyClubMembers);

				Log.d("MemberClubPoolId", "" + MemberClubPoolId);
				Log.d("MemberClubPoolName", "" + MemberClubPoolName);
				Log.d("MemberClubNoofMembers", "" + MemberClubNoofMembers);
				Log.d("MemberClubOwnerName", "" + MemberClubOwnerName);
				Log.d("MemberClubMembers", "" + MemberClubMembers);

				if (MyClubPoolName.size() > 0) {

					flag = 0;

					// contactsbtn.setVisibility(View.GONE);

					for (int i = 0; i < MyClubPoolName.size(); i++) {

						ClubObject cp = new ClubObject();

						cp.setName(MyClubPoolName.get(i).toString().trim());
						cp.setClubmembers(MyClubMembers.get(i).toString()
								.trim());

						cp.setNoofMembers(MyClubNoofMembers.get(i).toString()
								.trim());

						cp.setClubOwnerName("");

						ClubListClass.ClubList.add(cp);
					}

					adapterClubMy.notifyDataSetChanged();
				
				}

				if (MemberClubPoolName.size() > 0) {

					for (int i = 0; i < MemberClubPoolName.size(); i++) {

						ClubObject cp = new ClubObject();

						cp.setName(MemberClubPoolName.get(i).toString().trim());
						cp.setClubmembers(MemberClubMembers.get(i).toString()
								.trim());

						cp.setNoofMembers(MemberClubNoofMembers.get(i)
								.toString().trim());

						cp.setClubOwnerName(MemberClubOwnerName.get(i)
								.toString().trim());

						// ClubListClass.MemberClubList.add(cp);
						ClubListClass.ClubList.add(cp);
					}
					/////////////////////////////////////////////////adapterClubMy.notifyDataSetChanged();
					notifyAdapter();
			}
			}

			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*//**
	 * Ownername in list item of clubs
	 * Send button
	 * Get all data in strutural format
	 * send that data in onactivty result
	 * call every place and every where
	 * @param response
	 */
	private void parseGroupData(String response){
		try{
			if (!TextUtils.isEmpty(response) && response.equalsIgnoreCase("No Users of your Club")) {

				flag = 1;
				gpListener.onEmptyGroup();
			}else{
				if(response == null) 
					return; 
				groupList.clear();
				JSONArray subArray = new JSONArray(response);
				for (int i = 0; i < subArray.length(); i++) {
					OwnerModel ownerModel = new OwnerModel();
					if(!subArray.getJSONObject(i).isNull("PoolId")){
						ownerModel.setPoolId(subArray.getJSONObject(i).getString("PoolId"));
					}
					if(!subArray.getJSONObject(i).isNull("PoolName")){
						ownerModel.setPoolName(subArray.getJSONObject(i).getString("PoolName"));
					}
					if(!subArray.getJSONObject(i).isNull("OwnerNumber")){
						ownerModel.setOwnerNumber(subArray.getJSONObject(i).getString("OwnerNumber"));
					}
					if(!subArray.getJSONObject(i).isNull("OwnerName")){
						ownerModel.setOwnerName(subArray.getJSONObject(i).getString("OwnerName"));
					}
					if(!subArray.getJSONObject(i).isNull("OwnerNumber")){
						ownerModel.setOwnerNumber(subArray.getJSONObject(i).getString("OwnerNumber"));
					}
					if(!subArray.getJSONObject(i).isNull("NoofMembers")){
						ownerModel.setNumberOfMembers(subArray.getJSONObject(i).getString("NoofMembers"));
					}
					if(!subArray.getJSONObject(i).isNull("Members") && subArray.getJSONObject(i).getJSONArray("Members").length()>0){
						ArrayList<MemberModel> arrayList = new ArrayList<MemberModel>();
						JSONArray array = subArray.getJSONObject(i).getJSONArray("Members");
						for (int j = 0; j < array.length(); j++) {
							MemberModel memberModel = new MemberModel();
							if(!array.getJSONObject(j).isNull("FullName")){
								memberModel.setMemberName(array.getJSONObject(j).getString("FullName"));
							}
							
							if(!array.getJSONObject(j).isNull("MemberNumber")){
								memberModel.setMemberNumber(array.getJSONObject(j).getString("MemberNumber"));

							}
							arrayList.add(memberModel);
						}
						ownerModel.setMemberList(arrayList);
					}
					groupList.add(ownerModel);
					
				}
				notifyAdapter();
			}
		
		}catch(NullPointerException e){
			e.printStackTrace();
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	public void addUserToGroup(OwnerModel ownerModel) {
		selectedGroupList.add(ownerModel);
		gpListener.onGroupSelected(selectedGroupList);
	}

	


}
