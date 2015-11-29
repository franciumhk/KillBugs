package com.francium.app.projectf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.System.arraycopy;

public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        RealTimeMessageReceivedListener,
        RoomStatusUpdateListener,
        RoomUpdateListener,
        OnInvitationReceivedListener {

    public Thread mThread = new Thread();
    GameGLSurfaceView mGLSurfaceView;
    private MediaPlayer mp;

    /*
     * API INTEGRATION SECTION. This section contains the code that integrates
     * the game with the Google Play game services API.
     */

    final static String TAG = "DEBUG";
    final static String STAG = "SDEBUG";

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_UNUSED = 5001;

    // playing on hard mode?
    boolean mHardMode = false;

    // Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Set to true to automatically start the sign in flow when the Activity starts.
    // Set to false to require the user to click the button in order to sign in.
    private boolean mAutoStartSignInFlow = true;

    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    String mRoomId = null;

    // My participant ID in the currently active game
    String mOwnParticipantID = null;
    String mPeerParticipantID = null;

    // Are we playing in multiplayer mode?
    public static boolean mMultiplayer = false;

    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;


    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;

    // Message buffer for sending messages
    //[0] = cmd id
    //[1] - [4] = own score
    //[5] - [8] = own health point
    //[9] - [12] = attack point
    byte[] mMsgBuf = new byte[Configuration.MSG_SIZE];
    int mOwnScore = 0;
    int mOwnHealthPoint = 0;
    int mAttackPoint = 0;

    static boolean isGameRunning = false;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the Google Api Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        // set up a click listener for everything we care about
        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mGLSurfaceView = (GameGLSurfaceView) findViewById(R.id.glSurfaceViewID);
        Log.d("DEBUG", "mGLSurfaceView: " + mGLSurfaceView);

        mGLSurfaceView.requestFocus();
        mGLSurfaceView.setFocusableInTouchMode(true);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Log.d(TAG, "onClick");

        switch (v.getId()) {
            case R.id.button_single_player:
            case R.id.button_single_player_2:
                // play a single-player game
                resetGameVars();
                startGame(false);
                break;
            case R.id.button_sign_in:
                // user wants to sign in
                // Check to see the developer who's running this sample code read the instructions :-)
                // NOTE: this check is here only because this is a sample! Don't include this
                // check in your actual production app.
                if (!BaseGameUtils.verifySampleSetup(this, R.string.app_id)) {
                    Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
                }

                // start the sign-in flow
                Log.d(TAG, "Sign-in button clicked");
                mSignInClicked = true;
                mGoogleApiClient.connect();
                break;
            case R.id.button_sign_out:
                // user wants to sign out
                // sign out.
                Log.d(TAG, "Sign-out button clicked");
                mSignInClicked = false;
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                switchToScreen(R.id.screen_sign_in);
                break;
            case R.id.button_invite_players:
                // show list of invitable players
                intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 1);
                Log.d("DEBUG", "screen_wait: button_invite_players");
                switchToScreen(R.id.screen_wait);
                startActivityForResult(intent, RC_SELECT_PLAYERS);
                break;
            case R.id.button_see_invitations:
                // show list of pending invitations
                intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
                Log.d("DEBUG", "screen_wait: button_see_invitations");
                switchToScreen(R.id.screen_wait);
                startActivityForResult(intent, RC_INVITATION_INBOX);
                break;
            case R.id.button_accept_popup_invitation:
                // user wants to accept the invitation shown on the invitation popup
                // (the one we got through the OnInvitationReceivedListener).
                acceptInviteToRoom(mIncomingInvitationId);
                mIncomingInvitationId = null;
                break;
            case R.id.button_quick_game:
                // user wants to play against a random opponent right now
                startQuickGame();
                break;
            case R.id.button_show_achievements:
                Log.d(TAG, "onShowAchievementsRequested");
                onShowAchievementsRequested();
                break;
            case R.id.button_show_leaderboards:
                Log.d(TAG, "onShowLeaderboardsRequested");
                onShowLeaderboardsRequested();
                break;
        }
    }

    void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        Log.d("DEBUG", "screen_wait: startQuickGame");
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                // we got the result from the "select players" UI -- ready to create the room
                handleSelectPlayersResult(responseCode, intent);
                break;
            case RC_INVITATION_INBOX:
                // we got the result from the "select invitation" UI (invitation inbox). We're
                // ready to accept the selected invitation:
                handleInvitationInboxResult(responseCode, intent);
                break;
            case RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI.
                if (responseCode == Activity.RESULT_OK) {
                    // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).");
                    startGame(true);
                } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that they want to leave the room
                    leaveRoom();
                } else if (responseCode == Activity.RESULT_CANCELED) {
                    // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                    leaveRoom();
                }
                break;
            case RC_SIGN_IN:
                Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
                        + responseCode + ", intent=" + intent);
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (responseCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    BaseGameUtils.showActivityResultError(this,requestCode,responseCode, R.string.signin_other_error);
                }
                break;
        }
        super.onActivityResult(requestCode, responseCode, intent);
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.
    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Log.d(TAG, "Creating room...");
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        Log.d("DEBUG", "screen_wait: handleSelectPlayersResult");
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        acceptInviteToRoom(inv.getInvitationId());
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        Log.d("DEBUG", "screen_wait: acceptInviteToRoom");
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        return;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (mCurScreen != R.id.screen_game){
            super.onBackPressed();
            super.finish();
            return;
        } else {
            leaveRoom();
        }
    }

    // Activity is going to the background. We have to leave the current room.
    @Override
    public void onStop() {
        Log.d(TAG, "**** got onStop");

        GameEngine.mTimeHandler.pause();

        // if we're in a room, leave it.
        leaveRoom();

        // stop trying to keep the screen on
        stopKeepingScreenOn();

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            switchToScreen(R.id.screen_sign_in);
        }
        else {
            switchToScreen(R.id.screen_main);
        }
        mp.stop();
        super.onStop();
        super.finish();
        return;
    }

    // Activity just got to the foreground. We switch to the wait screen because we will now
    // go through the sign-in flow (remember that, yes, every time the Activity comes back to the
    // foreground we go through the sign-in flow -- but if the user is already authenticated,
    // this flow simply succeeds and is imperceptible).
    @Override
    public void onStart() {
        Log.d("DEBUG", "screen_wait: onStart");
        switchToScreen(R.id.screen_wait);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.w(TAG,
                    "GameHelper: client was already connected on onStart()");
        } else {
            Log.d(TAG,"Connecting client.");
            mGoogleApiClient.connect();
        }
        super.onStart();
        switchToMainScreen();
        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.s_background);
            mp.setVolume(0.1f, 0.1f);
            mp.setLooping(true);
            mp.start();
        }
    }

    // Leave the room.
    void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        mSecondsLeft = 0;
        stopKeepingScreenOn();
        if (mRoomId != null) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            mRoomId = null;
            Log.d(TAG, "Leave room: screen_wait");
            switchToScreen(R.id.screen_wait);
        } else {
            Log.d(TAG, "Leave room: switchToMainScreen");
            switchToMainScreen();
        }
    }

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);

        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    // Called when we get an invitation to play a game. We react by showing that to the user.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in
        // mIncomingInvitationId
        // and show the popup on the screen.
        mIncomingInvitationId = invitation.getInvitationId();
        ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                invitation.getInviter().getDisplayName() + " " +
                        getString(R.string.is_inviting_you));
        switchToScreen(mCurScreen); // This will show the invitation popup
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        if (mIncomingInvitationId.equals(invitationId)) {
            mIncomingInvitationId = null;
            switchToScreen(mCurScreen); // This will hide the invitation popup
        }
    }

    /*
     * CALLBACKS SECTION. This section shows how we implement the several games
     * API callbacks.
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected() called. Sign in successful!");

        Log.d(TAG, "Sign-in succeeded.");

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        if (connectionHint != null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null && inv.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Log.d(TAG,"onConnected: connection hint has a room invite!");
                acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        }
        switchToMainScreen();

        // if we have accomplishments to push, push them
        if (!mOutbox.isEmpty()) {
            pushAccomplishments();
            Toast.makeText(this, getString(R.string.your_progress_will_be_uploaded),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }

        switchToScreen(R.id.screen_sign_in);
    }

    // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
    // is connected yet).
    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedToRoom.");

        //get participants and my ID:
        mParticipants = room.getParticipants();
        mOwnParticipantID = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        Log.d(TAG, "My ID " + mOwnParticipantID);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
    }

    // Called when we've successfully left the room (this happens a result of voluntarily leaving
    // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        // we have left the room; return to main screen.
        Log.d(TAG, "onLeftRoom, code " + statusCode);
        switchToMainScreen();
    }

    // Called when we get disconnected from the room. We return to the main screen.
    @Override
    public void onDisconnectedFromRoom(Room room) {
        mRoomId = null;
        showGameError();
    }

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        switchToMainScreen();
    }

    // Called when room has been created
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError();
            return;
        }

        // save room ID so we can leave cleanly before the game starts.
        mRoomId = room.getRoomId();

        // show the waiting room UI
        showWaitingRoom(room);
    }

    // Called when room is fully connected.
    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }
        updateRoom(room);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }
        mRoomId = room.getRoomId();
        // show the waiting room UI
        showWaitingRoom(room);
    }

    // We treat most of the room update callbacks in the same way: we update our list of
    // participants and update the display. In a real game we would also have to check if that
    // change requires some action like removing the corresponding player avatar from the screen,
    // etc.
    @Override
    public void onPeerDeclined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onP2PDisconnected(String participant) {
    }

    @Override
    public void onP2PConnected(String participant) {
    }

    @Override
    public void onPeerJoined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> peersWhoLeft) {
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        updateRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) {
        updateRoom(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        mPeerParticipantID = peers.get(0);
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
            String roomId = room.getRoomId();
            Log.d(TAG, "roomId:" + roomId);
        }

    }

    /*
     * GAME LOGIC SECTION. Methods that implement the game's rules.
     */

    // Current state of the game:
    int mSecondsLeft = -1; // how long until the game ends (seconds)
    final static int GAME_DURATION = 20; // game duration, seconds.

    // Reset game variables in preparation for a new game.
    void resetGameVars() {
        mSecondsLeft = GAME_DURATION;
        mFinishedParticipants.clear();
    }

    public static long byteArrayToLeLong(byte[] encodedValue) {
        long value;
        value = (encodedValue[7] << (Byte.SIZE * 7));
        value |= (encodedValue[6] & 0xFF) << (Byte.SIZE * 6);
        value |= (encodedValue[5] & 0xFF) << (Byte.SIZE * 5);
        value |= (encodedValue[4] & 0xFF) << (Byte.SIZE * 4);
        value |= (encodedValue[3] & 0xFF) << (Byte.SIZE * 3);
        value |= (encodedValue[2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[1] & 0xFF) << (Byte.SIZE * 1);
        value |= (encodedValue[0] & 0xFF);
        return value;
    }

    long generateRandomSeed()
    {
        byte[] ownID = new byte[8];
        try {
            ownID = mOwnParticipantID.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] peerID = new byte[8];
        try {
            peerID = mPeerParticipantID.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        long seed = 0;
        int length = ownID.length < peerID.length ? peerID.length : ownID.length;
        length += 7;
        length /=8;
        length *= 8;
        byte[] combineID = new byte[length];
        for (int i = 0; i < length; i++){
            if (i < ownID.length)
                combineID[i] += ownID[i];
            if (i < peerID.length)
                combineID[i] += peerID[i];
        }
        for (int i = 8; i < length; i+=8){
            for (int j = 0; j < 8; j++) {
                combineID[j] += combineID[j+i];
            }
        }
        seed = byteArrayToLeLong(combineID);
        return seed;
    }


    // Start the gameplay phase of the game.
    void startGame(final boolean multiplayer) {
        Log.d(TAG, "startGame");
        mMultiplayer = multiplayer;
        broadcastStatus();

        if (multiplayer == true) {
            GameEngine.setRandomSeed(generateRandomSeed());
            Configuration.MAX_TIME = Configuration.MAX_TIME_MULTIPLAYER;
        }
        else {
            GameEngine.setRandomSeed(System.currentTimeMillis());
            Configuration.MAX_TIME = Configuration.MAX_TIME_SINGLE_PLAYER;
        }

        GameEngine.init();

        Message msg = new Message();
        msg.what = GameEngine.GAME_START;
        GameEngine.mHandler.sendMessage(msg);

        switchToScreen(R.id.screen_game);
        mGLSurfaceView.onStartRendering();

        isGameRunning = true;
        GameEngine.mFinalScore = false;
        GameEngine.mUpdatePeer = false;
        mThread = new Thread() {
            public void run() {
                while (isGameRunning) {
                    try {
                        if (GameEngine.mUpdatePeer == true) {
                            GameEngine.mUpdatePeer = false;
                            broadcastStatus();
                        }
                        if (GameEngine.mFinalScore == true) {
                            GameEngine.mFinalScore = false;
                            broadcastResult();
                            submitScore(GameEngine.mScoreHandler.getFinalOwnScore());
                        }
                        Thread.sleep(Configuration.DELAY_MS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mThread.start();
    }

    /*
     * COMMUNICATIONS SECTION. Methods that implement the game's network
     * protocol.
     */
    // Participants who sent us their final score.
    Set<String> mFinishedParticipants = new HashSet<String>();

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int byteArrayToLeInt(byte[] encodedValue) {
        int value = (encodedValue[3] << (Byte.SIZE * 3));
        value |= (encodedValue[2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[1] & 0xFF) << (Byte.SIZE * 1);
        value |= (encodedValue[0] & 0xFF);
        return value;
    }

    public static byte[] leIntToByteArray(int value) {
        byte[] encodedValue = new byte[Integer.SIZE / Byte.SIZE];
        encodedValue[3] = (byte) (value >> Byte.SIZE * 3);
        encodedValue[2] = (byte) (value >> Byte.SIZE * 2);
        encodedValue[1] = (byte) (value >> Byte.SIZE);
        encodedValue[0] = (byte) value;
        return encodedValue;
    }

    // Called when we receive a real-time message from the network.
    // Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
    // indicating
    // whether it's a final or interim score. The second byte is the score.
    // There is also the
    // 'S' message, which indicates that the game should start.
    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        if (buf[0] == 'F' || buf[0] == 'U') {
            // score update.
            byte[] tmp = new byte[4];
            int tmpValue;
            //Score
            arraycopy(buf, 1, tmp, 0, 4);
            tmpValue = byteArrayToLeInt(tmp);
            GameEngine.mScoreHandler.setPeerScore(tmpValue);
            //Health
            arraycopy(buf, 5, tmp, 0, 4);
            tmpValue = byteArrayToLeInt(tmp);
            GameEngine.mScoreHandler.setPeerHealthPoint(tmpValue);
            //Attack
            arraycopy(buf, 9, tmp, 0, 4);
            tmpValue = byteArrayToLeInt(tmp);
            if (tmpValue > 0) {
                GameEngine.mScoreHandler.decreaseOwnHealth(tmpValue);
                broadcastStatus();
            }

            // if it's a final score, mark this participant as having finished
            // the game
            if ((char) buf[0] == 'F') {
                //Score
                arraycopy(buf, 1, tmp, 0, 4);
                tmpValue = byteArrayToLeInt(tmp);
                GameEngine.mScoreHandler.setFinalPeerScore(tmpValue);
                //Health
                arraycopy(buf, 5, tmp, 0, 4);
                tmpValue = byteArrayToLeInt(tmp);
                GameEngine.mScoreHandler.setFinalPeerHealthPoint(tmpValue);

                if (GameEngine.mScene != Configuration.E_SCENARIO.RESULT){
                    Message msg = new Message();
                    msg.what = GameEngine.GAME_OVER;
                    GameEngine.mHandler.sendMessage(msg);
                }

                mFinishedParticipants.add(rtm.getSenderParticipantId());
            }
        }
    }

    // Broadcast my score to everybody else.
    void broadcastStatus() {
        if (!mMultiplayer)
            return; // playing single-player mode

        mMsgBuf = new byte[Configuration.MSG_SIZE];
        // First byte in message indicates whether it's a final score or not
        mMsgBuf[0] = (byte)'U';

        // Second byte is the score.
        mOwnScore = GameEngine.mScoreHandler.getOwnScore();
        mOwnHealthPoint = GameEngine.mScoreHandler.getOwnHealthPoint();
        mAttackPoint = GameEngine.mScoreHandler.getAttackPoint();

        constructBroadcastPacket();
        broadcast();
    }
    // Broadcast my score to everybody else.
    void broadcastResult() {
        if (!mMultiplayer)
            return; // playing single-player mode

        mMsgBuf = new byte[Configuration.MSG_SIZE];
        // First byte in message indicates whether it's a final score or not
        mMsgBuf[0] = (byte)'F';

        // Second byte is the score.
        mOwnScore = GameEngine.mScoreHandler.getFinalOwnScore();
        mOwnHealthPoint = GameEngine.mScoreHandler.getFinalOwnHealthPoint();
        mAttackPoint = GameEngine.mScoreHandler.getAttackPoint();

        constructBroadcastPacket();
        broadcast();
    }
    void constructBroadcastPacket()
    {
        byte[] tmp;
        tmp = leIntToByteArray(mOwnScore);
        arraycopy(tmp, 0, mMsgBuf, 1, 4);
        tmp = leIntToByteArray(mOwnHealthPoint);
        arraycopy(tmp, 0, mMsgBuf, 5, 4);
        tmp = leIntToByteArray(mAttackPoint);
        arraycopy(tmp, 0, mMsgBuf, 9, 4);
    }

    void broadcast(){
        // Send to every other participant.
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mOwnParticipantID))
                continue;
            if (p.getStatus() != Participant.STATUS_JOINED)
                continue;
            // final score notification must be sent via reliable message
            Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, mMsgBuf,
                    mRoomId, p.getParticipantId());
        }
    }

    /*
     * UI SECTION. Methods that implement the game's UI.
     */

    // This array lists everything that's clickable, so we can install click
    // event handlers.
    final static int[] CLICKABLES = {
            R.id.button_accept_popup_invitation, R.id.button_invite_players,
            R.id.button_quick_game, R.id.button_see_invitations, R.id.button_sign_in,
            R.id.button_sign_out,
            R.id.button_single_player,
            R.id.button_single_player_2,
            R.id.button_show_achievements,
            R.id.button_show_leaderboards
    };

    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.screen_game,
            R.id.screen_main,
            R.id.screen_sign_in,
            R.id.screen_wait
    };
    int mCurScreen = -1;

    void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        mCurScreen = screenId;

        // should we show the invitation popup?
        boolean showInvPopup;
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvPopup = false;
        } else if (mMultiplayer) {
            // if in multiplayer, only show invitation on main screen
            showInvPopup = (mCurScreen == R.id.screen_main);
        } else {
            // single-player: show on main screen and gameplay screen
            showInvPopup = (mCurScreen == R.id.screen_main || mCurScreen == R.id.screen_game);
        }
        findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
    }

    void switchToMainScreen() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            switchToScreen(R.id.screen_main);
        }
        else {
            switchToScreen(R.id.screen_sign_in);
        }
    }

    /*
     * MISC SECTION. Miscellaneous methods.
     */
    // Sets the flag to keep this screen on. It's recommended to do that during
    // the
    // handshake when setting up a game, because if the screen turns off, the
    // game will be
    // cancelled.
    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //======================================================================================================

    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                    RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    public void onShowLeaderboardsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),
                    RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
        }
    }

    public void submitScore(int score) {
        // check for achievements
        checkForAchievements(score);

        // update leaderboards
        updateLeaderboards(score);

        // push those accomplishments to the cloud, if signed in
        pushAccomplishments();
    }

    /**
     * Check for achievements and unlock the appropriate ones.
     *
     * @param score the score the user requested.
     */
    void checkForAchievements(int score) {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
        if (score >= Configuration.ACHIEVEMENT_LEVEL_1_SCORE) {
            mOutbox.mLevel1Achievement = true;
        }
        if (score >= Configuration.ACHIEVEMENT_LEVEL_2_SCORE) {
            mOutbox.mLevel2Achievement = true;
        }
        if (score >= Configuration.ACHIEVEMENT_LEVEL_3_SCORE) {
            mOutbox.mLevel3Achievement = true;
        }
        if (score >= Configuration.ACHIEVEMENT_LEVEL_4_SCORE) {
            mOutbox.mLevel4Achievement = true;
        }
        if (score >= Configuration.ACHIEVEMENT_LEVEL_5_SCORE) {
            mOutbox.mLevel5Achievement = true;
        }
        if (score >= Configuration.ACHIEVEMENT_LEVEL_6_SCORE) {
            mOutbox.mLevel6Achievement = true;
        }
    }

    void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            mOutbox.saveLocal(this);
            return;
        }
        if (mOutbox.mLevel1Achievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_id_1));
            mOutbox.mLevel1Achievement = false;
        }
        if (mOutbox.mLevel2Achievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_id_2));
            mOutbox.mLevel2Achievement = false;
        }
        if (mOutbox.mLevel3Achievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_id_3));
            mOutbox.mLevel3Achievement = false;
        }
        if (mOutbox.mLevel4Achievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_id_4));
            mOutbox.mLevel4Achievement = false;
        }
        if (mOutbox.mLevel5Achievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_id_5));
            mOutbox.mLevel5Achievement = false;
        }
        if (mOutbox.mLevel6Achievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_id_6));
            mOutbox.mLevel6Achievement = false;
        }
        if (mOutbox.mSinglePlayerModeScore >= 0) {
            Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_id_easy),
                    mOutbox.mSinglePlayerModeScore);
            mOutbox.mSinglePlayerModeScore = -1;
        }
        if (mOutbox.mMultiPlayerModeScore >= 0) {
            Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_id_hard),
                    mOutbox.mMultiPlayerModeScore);
            mOutbox.mMultiPlayerModeScore = -1;
        }
        mOutbox.saveLocal(this);
    }

    /**
     * Update leaderboards with the user's score.
     *
     * @param finalScore The score the user got.
     */
    void updateLeaderboards(int finalScore) {
        if (mMultiplayer && mOutbox.mMultiPlayerModeScore < finalScore) {
            mOutbox.mMultiPlayerModeScore = finalScore;
        } else if (!mMultiplayer && mOutbox.mSinglePlayerModeScore < finalScore) {
            mOutbox.mSinglePlayerModeScore = finalScore;
        }
    }

    class AccomplishmentsOutbox {
        boolean mLevel1Achievement = false;
        boolean mLevel2Achievement = false;
        boolean mLevel3Achievement = false;
        boolean mLevel4Achievement = false;
        boolean mLevel5Achievement = false;
        boolean mLevel6Achievement = false;
        int mSinglePlayerModeScore = -1;
        int mMultiPlayerModeScore = -1;

        boolean isEmpty() {
            return !mLevel1Achievement && !mLevel3Achievement && !mLevel4Achievement &&
                    !mLevel2Achievement && !mLevel5Achievement && !mLevel6Achievement && mSinglePlayerModeScore < 0 &&
                    mMultiPlayerModeScore < 0;
        }

        public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
        }

        public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
        }
    }

}
