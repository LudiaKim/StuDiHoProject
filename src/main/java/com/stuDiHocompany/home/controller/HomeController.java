package com.stuDiHocompany.home.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.stuDiHocompany.home.dao.IDao;
import com.stuDiHocompany.home.dto.Criteria;
import com.stuDiHocompany.home.dto.MemberDto;
import com.stuDiHocompany.home.dto.PageDto;
import com.stuDiHocompany.home.dto.QuestionListDto;
import com.stuDiHocompany.home.dto.ReservationListDto;

@Controller
public class HomeController {

	@Autowired
	private SqlSession sqlSession;
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
	
		return "main";
	}
	
	@RequestMapping(value = "/main")
	public String main() {
	
		return "main";
	}
	
	@RequestMapping(value = "/login") // 로그인 클릭하면
	public String login() {
	
		return "login"; // login.jsp로 이동
	}
	
	@RequestMapping(value = "/logout") // 로그아웃 클릭하면
	public String logout() {
	
		return "logout"; // logout.jsp로 이동
	}
	
	@RequestMapping(value = "/loginOk", method = RequestMethod.POST) // login.jsp에서 로그인 버튼 클릭하면
	public String loginOk(HttpServletRequest request, Model model) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		int checkIdFlag = dao.checkIdDao(request.getParameter("id")); // login.jsp에서 입력받은 아이디가 DB에 존재하면 1, 아니면 0 반환
		int checkPwFlag = dao.checkPwDao(request.getParameter("id"),request.getParameter("pw")); // 입력받은 아이디와 그 비밀번호가 일치하면 1, 아니면 0 반환
		model.addAttribute("checkIdFlag", checkIdFlag); // checkIdFlag=1이면 로그인하려는 아이디가 존재, 0이면 아이디가 존재하지 않음.
		model.addAttribute("checkPwFlag", checkPwFlag); // checkPwFlag=1이면 아이디와 그 아이디의 비밀번호가 일치하므로 로그인 가능
		
		if(checkIdFlag == 1 ) {
			
			MemberDto memberDto = dao.loginOkDao(request.getParameter("id"));
			
			HttpSession session = request.getSession();
			
			// 로그인 성공 시 세션에 id, pw 저장
			session.setAttribute("Id", memberDto.getMid()); 
			session.setAttribute("pw", memberDto.getMpw());
			session.setAttribute("name", memberDto.getMname());
			

		}
		
		
		return "loginOk"; // loginOk.jsp로 이동
	}
	
	@RequestMapping(value = "/join") // 회원가입 클릭하면
	public String join() {
	
		return "join"; // join.jsp로 이동
	}
	
	@RequestMapping(value = "/joinOk", method = RequestMethod.POST) // login.jsp에서 회원가입 버튼 클릭하면
	public String joinOk(HttpServletRequest request, Model model) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		int checkIdFlag = dao.checkIdDao(request.getParameter("id")); // join.jsp에서 입력받은 id가 DB에 존재하면 1, 아니면 0 반환
		model.addAttribute("checkIdFlag", checkIdFlag); // checkIdFlag=1이면 아이디 사용 중, 0이면 신규 가입가능
		
		if(checkIdFlag != 1) {
			dao.joinDao(request.getParameter("id"),request.getParameter("pw"), request.getParameter("name"), request.getParameter("phone"));
			
			HttpSession session = request.getSession();
			
			session.setAttribute("id", request.getParameter("id")); // 가입 성공된 아이디를 세션에 저장하여 로그인까지 하게 함
			
			model.addAttribute("mid", request.getParameter("id"));
			model.addAttribute("mname", request.getParameter("name"));
		}
		
		return "joinOk"; // 완료되면 joinOk.jsp로 이동
	}
	
	@RequestMapping(value = "/history") // 진료 이력
	public String history(HttpServletRequest request, Model model) {	
		
		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("Id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
	
		model.addAttribute("count",dao.countList(sessionId));
		model.addAttribute("list", dao.listDao(sessionId)); // 전체리스트
		
		model.addAttribute("count01",dao.count01List(sessionId));
		model.addAttribute("list01", dao.list01Dao(sessionId)); // 접종리스트
		
		model.addAttribute("count02",dao.count02List(sessionId));
		model.addAttribute("list02", dao.list02Dao(sessionId)); // 진료리스트
		
		model.addAttribute("count03",dao.count03List(sessionId));
		model.addAttribute("list03", dao.list03Dao(sessionId)); // 미용리스트
	
		return "history";
	}
	
	@RequestMapping(value = "/memberInfo") // 나의 정보 클릭 시
	public String memberInfo(HttpServletRequest request, Model model) {

		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		session.setAttribute("name", memberDto.getMname());
		model.addAttribute("memberDto",memberDto);
		
		
		return "memberInfo"; // memberInfo.jsp로 이동
	}

	@RequestMapping(value = "/infomodify") // 회원정보수정 클릭 시
	public String infomodify(HttpServletRequest request, Model model) {

		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
		
		return "infomodify"; // infomodify.jsp로 이동 // 정보수정/회원탈퇴 가능
	}	
	
	@RequestMapping(value="infomodifyOk") // infomodify.jsp에서 정보수정버튼 클릭 시 
	public String infoModifyOk(HttpServletRequest request, Model model) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		dao.InfoModifyOkDao(request.getParameter("pw"), request.getParameter("name"), request.getParameter("phone"),request.getParameter("id"));

		MemberDto memberDto = dao.loginOkDao(request.getParameter("id"));
		
		model.addAttribute("memberDto",memberDto);
		
		return "infomodifyOk"; // 완료 후 infomodifyOk.jsp로 이동
	}
	
	
	@RequestMapping(value="/memberCancel") // 회원 탈퇴 버튼 클릭 시
	public String membercancel(HttpServletRequest request, Model model) {
		
		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
		
		return "memberCancel"; // memberCancel.jsp 로 이동
	}
	
	@RequestMapping(value = "/membercancelOk", method = RequestMethod.POST) // 회원탈퇴 버튼 클릭시
	public String membercancelOk(HttpServletRequest request, Model model) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		int checkIdFlag = dao.checkIdDao(request.getParameter("id")); // join.jsp에서 입력받은 아이디가 DB에 존재하면 1, 아니면 0 반환
		int checkPwFlag = dao.checkPwDao(request.getParameter("id"),request.getParameter("pw")); // 입력받은 아이디와 그 비밀번호가 일치하면 1, 아니면 0 반환
		
		model.addAttribute("checkIdFlag", checkIdFlag); // checkIdFlag=1이면 로그인하려는 아이디가 존재, 0이면 아이디가 존재하지 않음.
		model.addAttribute("checkPwFlag", checkPwFlag); // checkPwFlag=1이면 아이디와 그 아이디의 비밀번호가 일치하므로 로그인 가능
		
		if(checkPwFlag == 1 ) {
			
			int check = dao.cancelDao(request.getParameter("id"),request.getParameter("pw"));
			
			dao.deleteAllDao(request.getParameter("id"));
			dao.qdeleteAllDao(request.getParameter("id"));
			HttpSession session = request.getSession();

			// 회원 정보 삭제 후 세션정보 삭제
			session.invalidate();
		}
		
		return "membercancelOk"; // 완료 후 membercancelOk.jsp로 이동
	}
	
	
	@RequestMapping(value = "/reservation") // 회원 예약 글쓰기 화면으로 이동
	public String mwrite(HttpServletRequest request, Model model) {

		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
		
		return "mwrite";
	}
	
	@RequestMapping(value = "/memberwrite")  // 회원 예약 글쓰기 화면에서 예약하기 버튼 눌렀을 때 memberwrite 실행 후 예약내역 리스트 페이지로 이동
	public String memberwrite(HttpServletRequest request) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		
		// DB에서 예약일자 가져와서
		// for(i=0;i>count;i++) {
		// db의 rdayof와 request.getParameter("rdayof")를 비교해서 같으면
		// 입력하신 날짜는 이미 예약 중입니다. 뜨고
		// 다르면 입력
		
		dao.writeDao(request.getParameter("rid"), request.getParameter("rname"),request.getParameter("rclass"), request.getParameter("rdayof"), request.getParameter("rtime"),request.getParameter("rcontent"), request.getParameter("rstatus"));
		
		return "redirect:history";
	}
	
	
	@RequestMapping(value="/mview") // 예약 내용 보기
	public String mview(HttpServletRequest request, Model model) {
		
		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
		model.addAttribute("mview", dao.viewDao(request.getParameter("rnum")));

		ReservationListDto rDto = dao.viewDao(request.getParameter("rnum"));
		request.setAttribute("Rstatus", rDto.getRstatus());
		request.setAttribute("Rtime", rDto.getRtime());
		
		return "mview";
	}
	
	
	@RequestMapping(value="/delete") // 예약 취소
	public String delete(HttpServletRequest request) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		dao.rcancelDao(request.getParameter("rstatus"), request.getParameter("rnum"));
		
		return "redirect:history";
	}
	
	@RequestMapping(value = "/map")
	public String map() {
	
		return "map";
	}
	
	// 1:1 문의
	/*@RequestMapping(value = "/QnA") // 문의 하기
	public String QnA(HttpServletRequest request, Model model) {	
		
		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
	
		model.addAttribute("count",dao.qcountList(sessionId));
		model.addAttribute("list", dao.qlistDao(sessionId));

	
		return "QnA";
	}*/
	
	   @RequestMapping(value = "/question")
	   public String question(HttpSession session, Model model) {	
			
		   String sessionId = (String) session.getAttribute("Id");
		   
		   model.addAttribute("memberId", sessionId);
	      
	      return "question";
	      

	   }
	   
	   @RequestMapping(value = "/questionOk")
	   public String questionOk(HttpServletRequest request) {
	      
	      String qid = request.getParameter("qid");//글쓴유저 아이디
	      String qname = request.getParameter("qname");//글쓴유저 이름
	      String qcontent = request.getParameter("qcontent");//글쓴 질문 내용
	      String qemail = request.getParameter("qemail");//글쓴유저 이메일
	      System.out.println(qid);
	      
	      IDao dao = sqlSession.getMapper(IDao.class);
	      
	      dao.writeQuestion(qid, qname, qcontent, qemail);
	      
	      return "redirect:list";
	   }

	   @RequestMapping(value = "/questionView")
	   public String questionView(HttpServletRequest request, Model model) {
	      
	      String qnum = request.getParameter("qnum");
	      
	      IDao dao = sqlSession.getMapper(IDao.class);
	      
	      QuestionListDto questionListDto = dao.questionView(qnum);
	      
	      model.addAttribute("qdto", questionListDto);
	      model.addAttribute("qid", questionListDto.getQid());//글쓴 유저의 id값 전송
	      
	      return "questionView";
	   }

	   @RequestMapping(value = "/questionModify")
	   public String questionModify(HttpServletRequest request, Model model) {
	      
	      String qnum = request.getParameter("qnum");
	      
	      IDao dao = sqlSession.getMapper(IDao.class);
	      
	      QuestionListDto questionListDto = dao.questionView(qnum);
	      
	      model.addAttribute("qdto", questionListDto);
	      
	      
	      return "questionModify";
	   }

	   public String questionModifyOk(HttpServletRequest request) {
	      
	      String qnum = request.getParameter("qnum");
	      String qname = request.getParameter("qname");
	      String qcontent = request.getParameter("qcontent");
	      String qemail = request.getParameter("qemail");
	      
	      IDao dao = sqlSession.getMapper(IDao.class);
	      
	      dao.questionModify(qnum, qname, qcontent, qemail);
	      
	      return "redirect:list";
	   }
	   
	   @RequestMapping(value = "/questionDelete")
	   public String questionDelete(HttpServletRequest request) {
	      
	      String qnum= request.getParameter("qnum");
	      
	      IDao dao = sqlSession.getMapper(IDao.class);
	      
	      dao.questionDelete(qnum);
	      
	      return "redirect:list";
	   }
	   
	   @RequestMapping(value = "/list")
	   public String list(Model model, Criteria cri, HttpServletRequest request) {
	      
	      int pageNumInt = 0;
	      if(request.getParameter("pageNum") == null) {
	         pageNumInt = 1;
	         cri.setPageNum(pageNumInt);
	         
	      } else {
	         pageNumInt = Integer.parseInt(request.getParameter("pageNum"));
	         cri.setPageNum(pageNumInt);
	      }
	      IDao dao = sqlSession.getMapper(IDao.class);
	      
	      int totalRecord = dao.boardAllCount();
	      
	      //cri.setPageNum();
	      
	      cri.setStartNum(cri.getPageNum()-1 * cri.getAmount());//해당 페이지의 시작번호를 설정
	      
	      PageDto pageDto = new PageDto(cri, totalRecord);
	      
	      List<QuestionListDto> qboardDtos = dao.questionList(cri);
	      
	      model.addAttribute("pageMaker", pageDto);
	      model.addAttribute("qdtos", qboardDtos);
	      model.addAttribute("currPage", pageNumInt);
	      
	      return "questionList";
	   }   

	//=================================================================================================
	// 관리자 모드
	
	/*@RequestMapping(value = "/admininfomodify") // 정보수정 클릭 시
	public String admininfomodify(HttpServletRequest request, Model model) {

		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
		
		return "admininfomodify"; // admininfomodify.jsp로 이동
	}	
	
	@RequestMapping(value = "/adminInfo") // 나의 정보 클릭 시
	public String adminInfoInfo(HttpServletRequest request, Model model) {

		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		session.setAttribute("name", memberDto.getMname());
		model.addAttribute("memberDto",memberDto);
		
		
		return "adminInfo"; // memberInfo.jsp로 이동
	}
	
	@RequestMapping(value = "/adminhistory") // 진료 이력
	public String adminhistory(HttpServletRequest request, Model model) {	
		
		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
	
		model.addAttribute("count",dao.AllcountList());
		model.addAttribute("list", dao.AlllistDao()); // 전체리스트
		
		model.addAttribute("count01",dao.Allcount01List());
		model.addAttribute("list01", dao.Alllist01Dao()); // 접종리스트
		
		model.addAttribute("count02",dao.Allcount02List());
		model.addAttribute("list02", dao.Alllist02Dao()); // 진료리스트
		
		model.addAttribute("count03",dao.Allcount03List());
		model.addAttribute("list03", dao.Alllist03Dao()); // 미용리스트
	
		return "adminhistory";
	}
	
	@RequestMapping(value="/adminmview") // 예약 내용 보기
	public String adminmview(HttpServletRequest request, Model model) {
		
		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
		model.addAttribute("mview", dao.viewDao(request.getParameter("rnum")));
		
		ReservationListDto rDto = dao.viewDao(request.getParameter("rnum"));
		request.setAttribute("Rstatus", rDto.getRstatus());
		request.setAttribute("Rtime", rDto.getRtime());
		
		return "adminmview";
	}
	
	@RequestMapping(value="/visitOK") // 예약 건 방문완료 시
	public String visitOK(HttpServletRequest request, Model model) {

		IDao dao = sqlSession.getMapper(IDao.class);
		

		dao.visitOkDao(request.getParameter("rstatus"), request.getParameter("rnum"));
		
		return "redirect:adminhistory";
	}
	
	@RequestMapping(value="/admindelete") // 예약 취소
	public String admindelete(HttpServletRequest request) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		dao.rcancelDao(request.getParameter("rstatus"), request.getParameter("rnum"));
		
		return "redirect:adminhistory";
	}
	
	@RequestMapping(value = "/adminQnA") // 문의 이력
	public String adminQnA(HttpServletRequest request, Model model) {	
		
		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
	
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
	
		model.addAttribute("count",dao.qAllcountList());
		model.addAttribute("list", dao.qAlllistDao());

	
		return "adminQnA";
	}
	
	@RequestMapping(value="/adminqview") // 문의 내용 보기
	public String adminqview(HttpServletRequest request, Model model) {
		
		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("id");
		
		IDao dao = sqlSession.getMapper(IDao.class);
		MemberDto memberDto = dao.loginOkDao(sessionId);
		
		model.addAttribute("memberDto",memberDto);
		model.addAttribute("qview", dao.qviewDao(request.getParameter("qnum")));
		
		QuestionListDto qDto = dao.qviewDao(request.getParameter("qnum"));
		request.setAttribute("answer", qDto.getQstatus());
		
		return "adminqview";
	}

	@RequestMapping(value = "/qreplyOk")  // adminqview에서 답변쓰기 완료 버튼 누른 후
	public String qreplyOk(HttpServletRequest request) {
		
		IDao dao = sqlSession.getMapper(IDao.class);

		dao.qreplyDao(request.getParameter("qanswer"), request.getParameter("qstatus"),request.getParameter("qnum"));
		
		return "redirect:adminQnA";
	}*/
	//관리자모드 끝================================
	@RequestMapping(value = "/gallery")  // 갤러리 1 페이지 이동
	public String gallery(HttpServletRequest request) {
		
		return "gallery";
	}
	
	@RequestMapping(value = "/gallery2")  // 갤러리 2 페이지 이동
	public String gallery2(HttpServletRequest request) {
		
		return "gallery2";
	}
	
	@RequestMapping(value = "/gallery3")  // 갤러리 3 페이지 이동
	public String gallery3(HttpServletRequest request) {
		
		return "gallery3";
	}
	
	@RequestMapping(value = "/company")  // 회사소개 페이지 이동
	public String company(HttpServletRequest request) {
		
		return "company";
	}
	//=================================================================================================	
	

	
}
