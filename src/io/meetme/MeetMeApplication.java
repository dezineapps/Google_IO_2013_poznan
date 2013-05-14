package io.meetme;

import io.meetme.database.DatabaseManager;
import io.meetme.database.DatabaseManager.OnUserAddedListener;
import io.meetme.database.User;
import io.meetme.restclient.RestClient;
import io.meetme.restclient.RestClient.RequestMethod;
import io.meetme.restclient.RestClient.Response;
import android.app.Application;
import android.os.SystemClock;
import android.util.Log;

public class MeetMeApplication extends Application implements
	OnUserAddedListener {

    @Override
    public void onCreate() {
	super.onCreate();

	DatabaseManager.init(this, R.xml.database);
	DatabaseManager.setOnUserAddedListener(this);
	DatabaseManager databaseManager = DatabaseManager.getInstance();

	databaseManager.add(User.meetUser("123", "Test"));

	Log.d(this.toString(), "POINTS: " + databaseManager.getPoints());
    }

    @Override
    public void onAdded(User user) {
	// TODO test it when server will be up :D
	// new Thread(new StatSender()).start();
    }

    private class StatSender implements Runnable {

	@Override
	public void run() {

	    RestClient restClient = new RestClient("www.google.pl",
		    RequestMethod.POST);

	    try {
		restClient.buildRequest();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    restClient.executeRequest();

	    Response response = restClient.getResponse();
	    if (response.httpCode != 666) {
		// Go sleep and retry when connection fails
		SystemClock.sleep(10000);
		// Pseudo recursive call :D
		StatSender statSender = new StatSender();
		statSender.run();
	    }
	}
    }
}
