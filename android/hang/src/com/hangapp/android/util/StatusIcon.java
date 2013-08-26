package com.hangapp.android.util;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.User;
import com.hangapp.android.network.rest.RestClient;

public class StatusIcon extends ImageButton implements OnClickListener {

	Context context;
	Database db;
	RestClient restClient;
	User user;

	public StatusIcon(Context context, Database db, RestClient restClient,
			User user) {
		super(context);

		this.context = context;
		this.restClient = restClient;
		this.db = db;
		this.user = user;

		this.setBackground(context.getResources().getDrawable(
				R.drawable.imagebutton_status_grey));
	}

	@Override
	public void onClick(View v) {
		if (db.getIncomingUser(user.getJid()).getAvailability().isActive()) {
			restClient.sendNudge(user.getJid());
			Toast.makeText(context, "Sending a nudge to " + user.getFirstName(),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context,
					user.getFirstName() + " have a status! Leave them alone!",
					Toast.LENGTH_SHORT).show();
		}
	}

}
