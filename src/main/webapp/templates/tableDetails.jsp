<%@include file="header.jsp" %>


	<div class="alert alert-info" role="alert">
    Search By Keyword functionality is WIP.
  </div>

	<legend>Table Name : ${tableName}</legend>
	<div class="row">
		<div class="col-sm">
			<table class="table table-condensed">
	      <thead>
	        <th>Table Names</th>
	        <th>Actions</th>
	      </thead>
	      <tbody>
	        <c:forEach items="${tableDetails}" var="details">
	          <tr>
	            <td>
	              <strong>
	                <c:out value="${details.name}"/>
	                <c:if test="${details['primary-key'] != null}">
	                  *
	                </c:if>
	              </strong>
	              <span><i><c:out value="${details['data-type']}"/>
	                <c:choose>
	                  <c:when test="${details['column-size'] != null}">
	                    (<c:out value="${details['column-size']}"/>)</i>
	                  </c:when>
	                  <c:otherwise>
	                      (11)</i>
	                  </c:otherwise>
	                </c:choose>
	              </span>
	            </td>
	            <td><input type="text" class="form-control"></td>
	          </tr>
	        </c:forEach>
	      </tbody>
	    </table>
	  </div>
  </div>
  <div class="row">
		<div class="col-xs-6 offset-sm-6">
			<form method="post" action="getRecords">
				<input type="hidden" name="viewRecords" value="${viewRecords}"/>
				<input type="hidden" name="tableName" value="${tableName}"/>
				<button type="submit" class="btn btn-primary">Find</button>
				<button type="button" class="btn btn-primary">Refresh</button>
			</form>
		</div>
	</div>

	<table class="table" style="margin-top: 20px;">
		<thead>
      <c:forEach items="${tableDetails}" var="details">
        <th>
          <c:out value="${details.name}"/>
        </th>
      </c:forEach>
    </thead>
    <tbody>
      <c:forEach items="${records}" var="record">
        <tr>
          <c:forEach items="${tableDetails}" var="details">
            <td><c:out value="${record[details.name]}"/></td>
          </c:forEach>
        </tr>
      </c:forEach>
    </tbody>
	</table>


<%@include file="footer.jsp" %>