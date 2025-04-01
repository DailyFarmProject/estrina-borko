package farming.api;

public interface UserApi {
     String BASE = "/api";
     String ME = "/user/me";
     String PASSWORD = "/password";
     String TYPE = "/userType/{login}";
     String DELETE = "/user/{login}";
     String REVOKE = "/revoke/{login}";
     String ACTIVATE = "/activate/{login}";
     String PASSWORD_HASH = "/password/{login}";
     String ACTIVATION_DATE =  "/activation_date/{login}";
     String HOME = "/home";
     String ADMIN_TEST = "/admin/test";
}