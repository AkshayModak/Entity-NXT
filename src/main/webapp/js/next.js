var callAjax = function(url) {
	$.ajax({
    url: url,
    type: "post",
		success: function( data ) {
			displaySuccess(data);
		},
		error:function( data ) {
      displaySuccess(data);
		}
  });
}

var displaySuccess = function(message) {
		$("#result").html('<div class="alert alert-success alert-dismissible fade show">' +
      '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
      '<span aria-hidden="true">&times;</span></button>' + message + '</div>');
}

var displayError = function(message) {
		$("#result").html('<div class="alert alert-danger alert-dismissible fade show">' +
      '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
      '<span aria-hidden="true">&times;</span></button>' + message + '</div>');
}

var showMessage = function(status, message) {
alert(showMessage);
		if ("error" == status) {
				displayError(message);
		} else {
				displaySuccess(message);
		}
}