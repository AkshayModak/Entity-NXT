<c:set var="uri" value="${requestScope['javax.servlet.forward.request_uri']}"/>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
  <a class="navbar-brand" href="#">Dashboard</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse" id="navbarSupportedContent">
    <ul class="navbar-nav mr-auto">
      <li class="nav-item active">
        <a class="nav-link" href="/index.jsp">Home <span class="sr-only">(current)</span></a>
      </li>
      <li class="nav-item active">
        <a class="nav-link" href="#">Import</a>
      </li>
    </ul>
    <ul class="navbar-nav">
      <li class="nav-item active">
				<button type="submit" class="btn btn-link nav-link" onClick="callAjax('run')">Refresh Entities</button>
      </li>
    </ul>
  </div>
</nav>