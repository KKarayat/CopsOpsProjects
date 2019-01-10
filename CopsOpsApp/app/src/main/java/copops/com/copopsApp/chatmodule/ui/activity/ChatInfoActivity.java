package copops.com.copopsApp.chatmodule.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.users.model.QBUser;

import java.util.List;

import copops.com.copopsApp.R;
import copops.com.copopsApp.chatmodule.ui.adapter.UsersAdapter;
import copops.com.copopsApp.chatmodule.utils.chat.ChatHelper;
import copops.com.copopsApp.chatmodule.utils.qb.QbUsersHolder;

public class ChatInfoActivity extends BaseActivity implements UsersAdapter.clickPos{
    private static final String EXTRA_DIALOG = "dialog";
    UsersAdapter.clickPos mClickPos;
    private ListView usersListView;
    private QBChatDialog qbDialog;

    public static void start(Context context, QBChatDialog qbDialog) {
        Intent intent = new Intent(context, ChatInfoActivity.class);
        intent.putExtra(EXTRA_DIALOG, qbDialog);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        actionBar.setDisplayHomeAsUpEnabled(true);
        usersListView = _findViewById(R.id.list_login_users);
        qbDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG);
        mClickPos=this;
        getDialog();
    }

    private void getDialog() {
        String dialogID = qbDialog.getDialogId();
        ChatHelper.getInstance().getDialogById(dialogID, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                qbDialog = qbChatDialog;
                buildUserList();
            }

            @Override
            public void onError(QBResponseException e) {
                Toaster.shortToast(e.getMessage());
                finish();
            }
        });
    }

    @Override
    protected View getSnackbarAnchorView() {
        return usersListView;
    }

    private void buildUserList() {
        List<Integer> userIds = qbDialog.getOccupants();
        List<QBUser> users = QbUsersHolder.getInstance().getUsersByIds(userIds);
        UsersAdapter adapter = new UsersAdapter(this, users,mClickPos);
        usersListView.setAdapter(adapter);
    }

    @Override
    public void clickPostion() {

    }
}