package com.noye.pairgoal;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("/user/signup")
    Call<Void> createUser(@Body User user);

    @POST("/user/signin")
    Call<User> signinUser(@Body User user);

    @GET("/user/profile")
    Call<User> doGetUser(@Body User user);

    @PUT("/user/profile")
    Call<User> doPutUser(@Body User user);

    @GET("/goal")
    Call<GoalRes> doGetGoals(@Header("Access-Token") String token, @Query("doing") boolean doing);

    @POST("/goal")
    Call<ImageUrl> doPostGoal(@Header("Access-Token") String token, @Body Goal goal);

    @GET("/goal/{id}")
    Call<Goal> doGetGoal(@Path("id") String id);

    @PUT("/goal/{id}")
    Call<Goal> doPutGoal(@Path("id") String id);

    @DELETE("/goal/{id}")
    Call<Goal> doDeleteGoal(@Path("id") String id);

    @GET("/goal/{goalId}/diary/{date}")
    Call<Diary> doGetDiary(@Header("Access-Token") String token, @Path("goalId") String goalId, @Path("date") String date);

    @PUT("/goal/{goalId}/diary/{date}")
    Call<Void> doPutDiary(@Header("Access-Token") String token, @Path("goalId") String goalId, @Path("date") String date, @Body Diary diary);

    @GET("/conversation?offset=0&limit=10")
    Call<ArrayList<Conversation>> doGetConversations(@Header("Access-Token") String token);

    @GET("/conversation/{id}")
    Call<ArrayList<Message>> doGetMessages(@Header("Access-Token") String token, @Path("id") String id);

    @POST("/conversation/{id}")
    Call<Void> doPostMessage(@Header("Access-Token") String token, @Path("id") String id, @Body Message message);

    @GET("/conversation/message/{id}")
    Call<Message> doGetMessage(@Header("Access-Token") String token, @Path("id") String id);
}

class User {
    private String userId, nickname, password, token;

    User(String token) {
        this.token = token;
    }

    User(String userId, String nickname, String password) {
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}

class Goal {
    private String id, title, category_num, image_path, start_date, end_date, partner_name;

    Goal(String id, String title, String category_num, String start_date, String end_date, String image_path, String partner_name) {
        this.id = id;
        this.title = title;
        this.category_num = category_num;
        this.image_path = image_path;
        this.start_date = start_date;
        this.end_date = end_date;
        this.partner_name = partner_name;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory_num() {
        return category_num;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getPartner_name() {
        return partner_name;
    }
}

class ImageUrl {
    private String url;

    ImageUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}

class GoalRes {
    private ArrayList<Goal> goals;
    private ArrayList<String> urls;

    GoalRes(ArrayList<Goal> goals, ArrayList<String> urls) {
        this.goals = goals;
        this.urls = urls;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }
}

class Diary {
    private String id, pre_id, next_id, score, comment, image_path;

    Diary(String id, String comment, String score, String pre_id, String next_id, String image_path) {
        this.id = id;
        this.pre_id = pre_id;
        this.next_id = next_id;
        this.score = score;
        this.comment  = comment;
        this.image_path = image_path;
    }

    public String getId() {
        return id;
    }

    public String getPre_id() {
        return pre_id;
    }

    public String getNext_id() {
        return next_id;
    }

    public String getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public String getImage_path() {
        return image_path;
    }
}

class Conversation {
    private Oid id;
    private ArrayList<Participant> participants;

    Conversation(Oid id, ArrayList<Participant> participants) {
        this.id = id;
        this.participants = participants;
    }

    public Oid getId() {
        return id;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    class Participant {
        private Oid _id;
        private String nickname, title;

        Participant(Oid _id, String nickname, String title) {
            this._id = _id;
            this.nickname = nickname;
            this.title = title;
        }

        public Oid get_id() {
            return _id;
        }

        public String getNickname() {
            return nickname;
        }

        public String getTitle() {
            return title;
        }
    }

    class Oid {
        private String $oid;

        Oid(String $oid) {
            this.$oid = $oid;
        }

        public String get$oid() {
            return $oid;
        }
    }
}

class Message {
    private Oid conversation_id;
    String sender, content, created_at;

    Message(Oid conversation_id, String sender, String content, String created_at) {
        this.conversation_id = conversation_id;
        this.sender = sender;
        this.content = content;
        this.created_at = created_at;
    }

    public Oid getConversation_id() {
        return conversation_id;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getCreated_at() {
        return created_at;
    }

    class Oid {
        private String $oid;

        Oid(String $oid) {
            this.$oid = $oid;
        }

        public String get$oid() {
            return $oid;
        }
    }
}
