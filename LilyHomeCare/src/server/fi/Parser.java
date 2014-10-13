package server.fi;

import model.Customer;
import model.Tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Parser {

	public static Customer parse(String content) throws JSONException {

		Customer customerTask = null;
		Tasks task;
		JSONObject userObject = new JSONObject(content);
		if (!userObject.optString("0").equals("")) {
			customerTask = new Customer();
			JSONObject object = new JSONObject(userObject.optString("0"));
			customerTask.setFirstName(object.getString("FirstName"));
			customerTask.setLastName(object.getString("LastName"));
			customerTask.setPhoneNumber(object.getString("PhoneNumber"));
			customerTask.setTagId(object.getString("TagId"));
			customerTask.setAddress(object.getString("Address"));
			
			
			if (!userObject.optString("tasks").equalsIgnoreCase("")) {
				JSONArray array = new JSONArray(userObject.getString("tasks"));

				for (int i = 0; i < array.length(); i++) {
					task = new Tasks();
					JSONObject taskObject = array.getJSONObject(i);
					task.setTaskName(taskObject.getString("TaskName"));
					task.setTaskTagID(taskObject.getString("TaskTagID"));
					task.setCareGiver(taskObject.getString("CareGiver"));
					task.setTaskDetail(taskObject.getString("TaskDetail"));
					customerTask.add(task);
				}

			}
		}

		return customerTask;
	}

}
