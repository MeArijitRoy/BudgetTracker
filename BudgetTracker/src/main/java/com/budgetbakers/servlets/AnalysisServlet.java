package com.budgetbakers.servlets;

import com.budgetbakers.entities.User;
import com.budgetbakers.services.RecordService;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/AnalysisServlet")
public class AnalysisServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AnalysisServlet.class);
	private final RecordService recordService = new RecordService();
	private final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("user") : null;

		if (user == null) {
			logger.warn("Unauthorized API access attempt to AnalysisServlet.");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to access this data.");
			return;
		}

		try {
			int userId = user.getId();

			String graphType = request.getParameter("graphType");
			String dateRange = request.getParameter("dateRange");
			String currency = request.getParameter("currency");
			String[] accountIdsParam = request.getParameterValues("accounts[]");

			List<Integer> accountIds = null;
			if (accountIdsParam != null) {
				accountIds = Arrays.stream(accountIdsParam).map(Integer::parseInt).collect(Collectors.toList());
			}

			Object dataForChart = null;

			switch (graphType) {
			case "spendingBreakdown":
				dataForChart = recordService.getSpendingByCategoryForAnalysis(userId, dateRange, accountIds, currency);
				break;
			case "cashFlowTrend":
				dataForChart = recordService.getCashFlowTrendForAnalysis(userId, dateRange, accountIds, currency);
				break;
			case "balanceTrend":
				dataForChart = recordService.getBalanceTrendForAnalysis(userId, dateRange, accountIds, currency);
				break;
			case "transactionList":
				dataForChart = recordService.getTransactionsForAnalysis(userId, dateRange, accountIds, currency);
				break;
			default:
				logger.warn("Invalid graph type requested: {}", graphType);
				throw new IllegalArgumentException("Invalid graph type requested.");
			}

			String jsonResponse = gson.toJson(dataForChart);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jsonResponse);

		} catch (Exception e) {
			logger.error("Error generating analysis data for user {}", user.getId(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An error occurred while generating the report data.");
		}
	}

}