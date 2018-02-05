<%@include file="templates/header.jsp" %>


	<table class="table table-condensed">
		<thead>
			<th>Table Names</th>
			<th>Actions</th>
		</thead>
		<tbody>
			<c:forEach items="${tables}" var="table">
        <tr>
          <td>
            <form method="post" name="showTable" action="showTable">
              <input type="hidden" name="tableName" value="<c:out value="${table}"/>"/>
              <input type="hidden" name="requestType" value="displayTable"/>
							<button type="submit" class="btn btn-link"><c:out value="${table}"/></button>
            </form>
          </td>
          <td></td>
        </tr>
      </c:forEach>
		</tbody>
	</table>


<%@include file="templates/footer.jsp" %>