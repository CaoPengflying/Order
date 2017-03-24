package Filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Admin;

public class SuperFilter  implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1,	FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1; 
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Admin admin = (Admin) request.getSession().getAttribute("user");
		if(admin==null){
			response.sendRedirect(request.getContextPath()+"/Jsps/login.jsp");
			return;
		}


		if (admin.getRole()!= Admin.ROLE_SUPERADMIN) {
			response.sendRedirect(request.getContextPath()+"/Jsps/error.jsp");
			return;
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

}
