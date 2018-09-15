package com.project.it.expert;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;

/**
 * Created by hashemi on 02/17/2018.
 */

public class SyncGetSelectJobs2 {
	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	SQLiteDatabase db_check;
	PublicVariable PV;
	InternetConnection IC;
	private Context activity;
	private String guid;
	private String hamyarcode;
	private String WsResponse;
	private String LastHamyarSelectUserServiceCode;
	private boolean CuShowDialog=false;
	private boolean notifocationEnable;
	//Contractor
	public SyncGetSelectJobs2(Context activity, String guid, String hamyarcode, String LastHamyarSelectUserServiceCode) {
		this.activity = activity;
		this.guid = guid;
		this.LastHamyarSelectUserServiceCode=LastHamyarSelectUserServiceCode;
		this.hamyarcode=hamyarcode;
		this.notifocationEnable=notifocationEnable;
		IC = new InternetConnection(this.activity.getApplicationContext());
		PV = new PublicVariable();

		dbh=new DatabaseHelper(this.activity.getApplicationContext());
		try {

			dbh.createDataBase();

		} catch (IOException ioe) {

			throw new Error("Unable to create database");

		}

		try {

			dbh.openDataBase();

		} catch (SQLException sqle) {

			throw sqle;
		}
	}

	public void AsyncExecute()
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
				SyncGetSelectJobs2.AsyncCallWS task = new SyncGetSelectJobs2.AsyncCallWS(this.activity);
				task.execute();
			}
			catch (Exception e) {

				e.printStackTrace();
			}
		}
		else
		{
			//Toast.makeText(this.activity.getApplicationContext(), "لطفا ارتباط شبکه خود را چک کنید", Toast.LENGTH_SHORT).show();
		}
	}

	//Async Method
	private class AsyncCallWS extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private Context activity;

		public AsyncCallWS(Context activity) {
			this.activity = activity;
			this.dialog = new ProgressDialog(this.activity);
			this.dialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try
			{
				CallWsMethod("GetHamyarUserServiceSelect");
			}
			catch (Exception e) {
				result = e.getMessage().toString();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				if (WsResponse.toString().compareTo("ER") == 0) {
//					Toast.makeText(this.activity.getApplicationContext(), "خطا در ارتباط با سرور", Toast.LENGTH_LONG).show();

				} else if (WsResponse.toString().compareTo("0") == 0) {
//					Toast.makeText(this.activity.getApplicationContext(), "متخصص شناسایی نشد!", Toast.LENGTH_LONG).show();
				} else {
					InsertDataFromWsToDb(WsResponse);
				}
			} else {
				//Toast.makeText(this.activity, "ط®ط·ط§ ط¯ط± ط§طھطµط§ظ„ ط¨ظ‡ ط³ط±ظˆط±", Toast.LENGTH_SHORT).show();
			}
			try {
				if (this.dialog.isShowing()) {
					this.dialog.dismiss();
				}
			} catch (Exception e) {
			}
		}

		@Override
		protected void onPreExecute() {
			if (CuShowDialog) {
				this.dialog.setMessage("در حال پردازش");
				this.dialog.show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

	}


	public void CallWsMethod(String METHOD_NAME) {
		//Create request
		SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
		PropertyInfo GuidPI = new PropertyInfo();
		//Set Name
		GuidPI.setName("GUID");
		//Set Value
		GuidPI.setValue(this.guid);
		//Set dataType
		GuidPI.setType(String.class);
		//Add the property to request object
		request.addProperty(GuidPI);
		//*****************************************************
		PropertyInfo HamyarCodePI = new PropertyInfo();
		//Set Name
		HamyarCodePI.setName("HamyarCode");
		//Set Value
		HamyarCodePI.setValue(this.hamyarcode);
		//Set dataType
		HamyarCodePI.setType(String.class);
		//Add the property to request object
		request.addProperty(HamyarCodePI);
		//*****************************************************
		PropertyInfo LastUserServiceCodePI = new PropertyInfo();
		//Set Name
		LastUserServiceCodePI.setName("LastHamyarUserServiceCode");
		//Set Value
		LastUserServiceCodePI.setValue(this.LastHamyarSelectUserServiceCode);
		//Set dataType
		LastUserServiceCodePI.setType(String.class);
		//Add the property to request object
		request.addProperty(LastUserServiceCodePI);
		//Create envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		//Set output SOAP object
		envelope.setOutputSoapObject(request);
		//Create HTTP call object
		HttpTransportSE androidHttpTransport = new HttpTransportSE(PV.URL);
		try {
			//Invoke web service
			androidHttpTransport.call("http://tempuri.org/" + METHOD_NAME, envelope);
			//Get the response
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			//Assign it to FinalResultForCheck static variable
			WsResponse = response.toString();
			if (WsResponse == null) WsResponse = "ER";
		} catch (Exception e) {
			WsResponse = "ER";
			e.printStackTrace();
		}
	}


	public void InsertDataFromWsToDb(String AllRecord) {
		String[] res;
		String[] value;
		String query = null;
		res = WsResponse.split("@@");
		db = dbh.getWritableDatabase();
		for (int i = 0; i < res.length; i++) {
			value = res[i].split("##");
			if(Check_Null(value[0]))
			{
				query = "UPDATE BsUserServices SET Status='1' WHERE Code_BsUserServices='" + value[0] + "'";
				db.execSQL(query);
			}
			else
			{
				query = "INSERT INTO BsUserServices (" +
						"Code_BsUserServices," +
						"UserCode," +
						"UserName," +
						"UserFamily," +
						"ServiceDetaileCode," +
						"MaleCount," +
						"FemaleCount," +
						"StartDate," +
						"EndDate," +
						"AddressCode," +
						"AddressText," +
						"Lat," +
						"Lng," +
						"City," +
						"State," +
						"Description," +
						"IsEmergency," +
						"InsertUser," +
						"InsertDate," +
						"StartTime," +
						"EndTime," +
						"HamyarCount," +
						"PeriodicServices," +
						"EducationGrade," +
						"FieldOfStudy," +
						"StudentGender," +
						"TeacherGender," +
						"EducationTitle," +
						"ArtField," +
						"CarWashType," +
						"CarType," +
						"Language," +
						"ArtFieldOther," +
						"UserPhone," +
						"Status" +
						") VALUES('" +
						value[0] +
						"','" + value[1] +
						"','" + value[2] +
						"','" + value[3] +
						"','" + value[4] +
						"','" + value[5] +
						"','" + value[6] +
						"','" + value[7] +
						"','" + value[8] +
						"','" + value[9] +
						"','" + value[10] +
						"','" + value[11] +
						"','" + value[12] +
						"','" + value[13] +
						"','" + value[14] +
						"','" + value[15] +
						"','" + value[16] +
						"','" + value[17] +
						"','" + value[18] +
						"','" + value[19] +
						"','" + value[20] +
						"','" + value[21] +
						"','" + value[22] +
						"','" + value[23] +
						"','" + value[24] +
						"','" + value[25] +
						"','" + value[26] +
						"','" + value[27] +
						"','" + value[28] +
						"','" + value[29] +
						"','" + value[30] +
						"','" + value[31] +
						"','" + value[32] +
						"','" + value[33] +
						"' , '1')";
				db.execSQL(query);
			}

		}
		db.close();
	}
	public boolean Check_Null(String Code)
	{
		db=dbh.getReadableDatabase();
		Cursor cursor=db.rawQuery("SELECT * FROM BsUserServices WHERE Code_BsUserServices='" +Code +"'",null);
		if(cursor.getCount()>0) {
			return true;
		}
		else
		{
			return false;
		}
	}
}
