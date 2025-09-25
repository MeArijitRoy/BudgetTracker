<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- This is a self-contained component for the "Add Record" pop-up modal. --%>
<%-- It should be included at the end of the main JSP's body. --%>
<%-- The styles for this are in records.css and it's controlled by records.js --%>
<div class="modal-backdrop" id="addRecordModal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Add New Record</h2>
            <span class="close-btn" id="closeModalBtn">&times;</span>
        </div>
        <div class="modal-body">
            <form action="<c:url value='/AddRecordServlet' />" method="post">
                
                <div class="form-group transaction-type-group">
                    <label class="type-radio">
                        <input type="radio" name="type" value="Expense" checked>
                        <span>Expense</span>
                    </label>
                    <label class="type-radio">
                        <input type="radio" name="type" value="Income">
                        <span>Income</span>
                    </label>
                    <label class="type-radio">
                        <input type="radio" name="type" value="Transfer">
                        <span>Transfer</span>
                    </label>
                </div>

                <div class="form-group">
                    <label for="amount">Amount</label>
                    <input type="number" id="amount" name="amount" step="0.01" placeholder="0.00" required>
                </div>
                                
                <div class="form-group">
                    <label for="account">Account</label>
                    <select id="account" name="account" required>
                        <option value="">-- Select Account --</option>
                        <c:forEach var="acc" items="${accounts}">
                            <option value="${acc.id}">${acc.name}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="category">Category</label>
                    <select id="category" name="category">
                        <option value="">-- No Category --</option>
                         <%-- The 'categories' list is provided by RecordsServlet --%>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.id}">${cat.name}</option>
                        </c:forEach>
                    </select>
                </div>
                

                <div class="form-group">
                    <label for="labels">Labels</label>
                    <input type="text" id="labels" name="labels" placeholder="e.g., vacation, business (comma separated)">
                </div>
                
                <div class="form-group">
                    <label for="date">Date</label>
                    <input type="date" id="date" name="date" required>
                </div>
                
                <div class="form-group">
                    <label for="note">Note</label>
                    <textarea id="note" name="note" rows="3" placeholder="Describe your record..."></textarea>
                </div>

                <div class="modal-footer">
                    <button type="submit" class="submit-btn">Add Record</button>
                </div>
            </form>
        </div>
    </div>
</div>

