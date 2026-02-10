package pcy.study.server.utils;

import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";
    private static final String LOGIN_ADMIN_ID = "LOGIN_ADMIN_ID";

    private SessionUtil() {
    }

    public static void setLoginUserId(HttpSession session, Long id) {
        session.setAttribute(LOGIN_USER_ID, id);
    }

    public static Long getLoginUserId(HttpSession session) {
        return (Long) session.getAttribute(LOGIN_USER_ID);
    }

    public static void setLoginAdminId(HttpSession session, Long id) {
        session.setAttribute(LOGIN_ADMIN_ID, id);
    }

    public static Long getLoginAdminId(HttpSession session) {
        return (Long) session.getAttribute(LOGIN_ADMIN_ID);
    }

    public static void clear(HttpSession session) {
        session.invalidate();
    }
}
