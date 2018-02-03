var callAjax = function(url) {
		$.ajax({
      url: url,
      type: "post"
    }).done(function() {
      alert("success");
    });
}