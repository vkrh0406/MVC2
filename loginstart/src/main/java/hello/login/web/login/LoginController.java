package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    //@PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "lohin/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());


        if (loginMember == null) {
            bindingResult.reject("LoginFail", "아이디 또는 비밀번호가 맞지 않습니다");
            return "login/loginForm";
        }

        //로그인 성공

        //쿠키에 시간 정보를 주지 안으면 세션쿠키
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "lohin/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());


        if (loginMember == null) {
            bindingResult.reject("LoginFail", "아이디 또는 비밀번호가 맞지 않습니다");
            return "login/loginForm";
        }

        //로그인 성공

        //쿠키에 시간 정보를 주지 안으면 세션쿠키

        sessionManager.createSession(loginMember, response);


        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        sessionManager.expire(request);

        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
