<%@include file="header.jsp" %>

<div class="row pt-3">
	<div class="col-sm">
		<form method="post" action="executeQuery">
			<div class="form-group">
			  <label for="comment"><strong>Custom Query:</strong> (<small>Run Custom Query</small>)</label>
			  <textarea class="form-control" name="query" rows="5" id="comment"></textarea>
			</div>
			<button type="submit" class="btn btn-primary mb-3 btn-lg btn-block">Execute</button>
		</form>
	</div>
</div>

<%@include file="footer.jsp" %>