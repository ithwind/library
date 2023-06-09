package controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pojo.User;
import service.UserService;

import java.util.Map;


@Controller
public class UserController {

    @RequestMapping("/Home")
    public String toMainPge(){
        return "main";
    }

    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    HeaderController headerController;
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @RequestMapping("/login")
    public String login(String username, HttpServletRequest request){
        User u = new User();
        try {
            String password = request.getParameter("password");
            User user = userService.login(username);
            String rawPassword = user.getPassword();
            String role = user.getRole();
           //查询到用户
           if(encoder.matches(password, rawPassword)){
               u.setPassword(rawPassword);
               u.setUsername(username);
               u.setRole(role);
               request.getSession().setAttribute("USER_SESSION", u);
               headerController.uploadHeader(request);
               return "redirect:/Home";
           }else{
               request.setAttribute("msg", "用户名或密码错误");
               return "forward:/admin/login.jsp";
           }
       }catch (Exception e){
           request.setAttribute("msg", "系统异常请稍后再试");
           e.printStackTrace();
           return "forward:/admin/login.jsp";
       }
    }

    @RequestMapping("/register")
    public String register(String username, String password, HttpServletRequest request){
        if (request.getParameter("username") == null || request.getParameter("password")==null){
            request.setAttribute("msg", "用户名和密码不能为空，请重新输入");
            return "forward:/admin/register.jsp";
        }
        //密码加密
        password = encoder.encode(password);
        System.out.println(userService.register(username, password));
        return "redirect:/admin/login.jsp";
    }

    @RequestMapping("/logout")
    public String logout( HttpServletRequest request){
        try {
            HttpSession session = request.getSession();
            //销毁Session
            session.invalidate();
            return  "forward:/admin/login.jsp";
        }catch(Exception e){
            e.printStackTrace();
            request.setAttribute("msg","系统错误");
            return  "forward:/admin/login.jsp";
        }
    }
}
