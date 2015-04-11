/**
 * Custom Javascript.
 */

(function() {
	$('.form-datepicker').datepicker();
}());

$(function() {
	$('.datetimepicker').datetimepicker();
});

$(function() {
	$('.datetimepicker-time').datetimepicker({
		format : 'LT'
	});
});

function enableob(o) {
	eval(o + ".disabled = false");
}

function disableob(o) {
	eval(o + ".disabled = true");
}

function toggleform(formstr, chkobstr, obstr) {
	var checked = eval(formstr + "." + chkobstr + ".checked");
	var obs = obstr.split(",");
	for (i = 0; i < obs.length; i++) {
		obs[i] = formstr + "." + obs[i];
	}

	if (checked == false) {
		for (i = 0; i < obs.length; i++) {
			disableob(obs[i]);
		}
	} else {
		for (i = 0; i < obs.length; i++) {
			enableob(obs[i]);
		}
	}
}

$('#myModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget) // Button that triggered the modal
	var recipient = button.data('whatever') // Extract info from data-*
											// attributes
	var modal = $(this)
	modal.find('.modal-body input').val(recipient)
});

$("#myModal").on(
		'click',
		'.ajax-delete',
		function(event) {
			var entityId = $('#recipient-name').val();
			console.log(entityId);
			$.ajax({
				url : "delete/" + entityId,
				cache : false,
				type : "GET",
				success : function(response) {
					$(".modal-body-content").hide();
					$(".modal-body-content-ret").html(
							"<div class='success-msg'>" + response + "</div>");
					// append hidden field
					$(".modal-body-content-ret").append(
							"<input type=\"hidden\" class=\"form-control\" id=\"recipient-name\" value=\""
									+ entityId + "\"></div>");
					$(".modal-body-content-ret").show();
					// Hide Delete Button on pop-up window
					$("#myModal .ajax-delete").hide();
					// Hide deleted row visible behind pop-up window
					$("#row-" + entityId).hide();
				},
				error : function(xhr) {
					$(modal - body).html(
							"<div class='error-msg'>" + response + "</div>");
				}
			});
		});

$("#myModal").on('click', '.popup-close', function(event) {
	$("#myModal .ajax-delete").show();
	$(".modal-body-content").show();
	$(".modal-body-content-ret").hide();
});

$('#myModal').on('hide.bs.modal', function(event) {
	$("#myModal .ajax-delete").show();
	$(".modal-body-content").show();
	$(".modal-body-content-ret").hide();
});

$('.pending-approve').on('click', function(event) {
	var leaveApplicationId = $(this).find("input[type=hidden]").val();
	var pendingLeaveApplicationId = "pending-" + leaveApplicationId;
	console.log("User is #" + leaveApplicationId + "#");
	$.ajax({
		url : getRequestURL("leave/pending/approve/" + leaveApplicationId),
		cache : false,
		type : "GET",
		success : function(response) {
			console.log("AJAX Response is: " + response);
			$('#' + pendingLeaveApplicationId).hide();
		},
		error : function(xhr) {
			console.log("AJAX Error response is: " + xhr);
		}
	});
});

$('.pending-hold')
		.on(
				'click',
				function(event) {
					var leaveApplicationId = $(this).find("input[type=hidden]")
							.val();
					var pendingLeaveApplicationId = "pending-"
							+ leaveApplicationId;
					$
							.ajax({
								url : getRequestURL("leave/pending/hold/"
										+ leaveApplicationId),
								cache : false,
								type : "GET",
								success : function(response) {
									console
											.log("AJAX Response is: "
													+ response);
									console.log('#pending-status-'
											+ leaveApplicationId);
									$(
											'#pending-status-'
													+ leaveApplicationId
													+ " :nth-child(2)").html(
											"PENDING");
									// $('#abc').html("<div class='error-msg'>"
									// + response + "</div>");
								},
								error : function(xhr) {
									console.log("AJAX Error response is: "
											+ xhr);
								}
							});
				});

$('.pending-cancel').on('click', function(event) {
	var leaveApplicationId = $(this).find("input[type=hidden]").val();
	var pendingLeaveApplicationId = "pending-" + leaveApplicationId;
	console.log("User is #" + leaveApplicationId + "#");
	$.ajax({
		url : getRequestURL("leave/pending/cancel/" + leaveApplicationId),
		cache : false,
		type : "GET",
		success : function(response) {
			console.log("AJAX Response is: " + response);
			$('#' + pendingLeaveApplicationId).hide();
		},
		error : function(xhr) {
			console.log("AJAX Error response is: " + xhr);
		}
	});
});

$('#shiftButton').on('click', function() {
	if ($('#shiftId').val()) {
		stopShift($('#shiftId').val());
	} else {
		startShift();
	}
})

function startShift() {
	$.ajax({
		url : getRequestURL("start-shift"),
		cache : false,
		type : "POST",
		success : function(response) {
			$('#shiftId').val(response);
			$('#shiftButton').html('Stop Shift');
		},
		error : function() {
			$('#shiftButton').html('Start Shift');
		}

	});
}

function stopShift(shiftId) {
	$.ajax({
		url : getRequestURL("end-shift"),
		cache : false,
		type : "POST",
		success : function(response) {
			$('#shiftId').val(null);
			$('#shiftButton').html('Start Shift');
		},
		data : {
			'shiftId' : shiftId
		},
		error : function() {
			$('#shiftButton').html('Stop Shift');
		}

	});
}

// to be called from page load ..
function checkForActiveShift() {
	if ($('#shiftButton').length == 0) {
		return;
	}
	$.ajax({
		url : getRequestURL("get-shift-status"),
		cache : false,
		type : "GET",
		success : function(response) {
			$('#shiftId').val(response);
			$('#shiftButton').html('Stop Shift');
		},
		error : function() {
			$('#shiftButton').html('Start Shift');
		}

	});
}

(function() {
	initLeaveStatistics();
	initAdminStatistics();
	checkForActiveShift();
}());

function initLeaveStatistics() {
	if ($("#leave-statistics-table").length == 0) {
		return;
	}

	$.get(getRequestURL("get-agent-quick-statistics"), function(JSONData) {
		var obj = JSON.parse(JSON.stringify(JSONData));
		for ( var key in obj.data) {
			if (obj.data.hasOwnProperty(key)) {
				var row = $("#leave-statistics-table")[0].insertRow(-1);
				row.insertCell(0).innerHTML = key;
				row.insertCell(1).innerHTML = obj.data[key];
			}
		}
	});
}

function initAdminStatistics() {
	if ($("#admin-statistics-table").length == 0) {
		return;
	}

	$
			.get(getRequestURL("get-admin-quick-statistics"),
					function(JSONData) {
						var obj = JSON.parse(JSON.stringify(JSONData));
						for ( var key in obj.data) {
							if (obj.data.hasOwnProperty(key)) {
								var row = $("#admin-statistics-table")[0]
										.insertRow(-1);
								row.insertCell(0).innerHTML = key;
								row.insertCell(1).innerHTML = obj.data[key];

							}
						}
						// Update Top Navbar Pending Approvals Badge
						$("#leave-pending-approvals-badge").html(
								obj.data["Pending Approvals"]);
					});
}

/**
 * Utility functions.
 */

function getDataAjax(urlForData, requestData, callbackSuccess, callbackFail) {
	$.ajax({
		url : getRequestURL(urlForData),
		cache : false,
		data : requestData,
		type : "GET",
		success : callbackSuccess,
		error : callbackFail
	});
}

function getRequestURL(requestURL) {
	return (getBaseURL() + requestURL);
}

function getBaseURL() {
	var url = location.href; // entire url including querystring - also:
								// window.location.href;
	var baseURL = url.substring(0, url.indexOf('/', 14));

	/* if (baseURL.indexOf('http://localhost') != -1) */{
		// Base Url for localhost
		var url = location.href; // window.location.href;
		var pathname = location.pathname; // window.location.pathname;
		var index1 = url.indexOf(pathname);
		var index2 = url.indexOf("/", index1 + 1);
		var baseLocalUrl = url.substr(0, index2);

		return baseLocalUrl + "/";
	}
	/*
	 * else { // Root Url for domain name return baseURL + "/"; }
	 */

}

$('#update-password-button').click(function() {
	$.ajax({
		url : getRequestURL("user-profile/update-user-password"),
		cache : false,
		dataType : 'json',
		contentType : 'application/json',
		mimeType : 'application/json',
		data : JSON.stringify({
			userId : $('#userId').val(),
			newPassword : $('#new-password').val(),
			reEnteredPassword : $('#re-entered-password').val()
		}),
		type : "POST",
		success : function(data) {
			$('#update-password-success').html(data);
		},
		error : function(data) {
			$('#update-password-error').html("Operation failed");
		}
	});
});

$('#update-user-role-button').click(function() {
	$.ajax({
		url : getRequestURL("user-profile/update-user-role"),
		cache : false,
		dataType : 'json',
		contentType : 'application/json',
		mimeType : 'application/json',
		data : JSON.stringify({
			userId : $('#userId').val(),
			userRole : $('#userRoleList :selected').text(),
		}),
		type : "POST",
		success : function(data) {
			$('#update-user-role-success').html(data);
		},
		error : function(data) {
			$('#update-user-role-error').html("Operation failed");
		}
	});
});

$('#modifyUserShiftButton').on(
		'click',
		function() {
			var shiftId = $('#modifyUserShiftButton').children("input").val();
			var shiftStartTime = $('#modifyUserShiftButton').parent().parent()
					.siblings(".shiftStartTime").children("input").val();
			var shiftEndTime = $('#modifyUserShiftButton').parent().parent()
					.siblings(".shiftEndTime").children("input").val();
			$.ajax({
				url : getRequestURL("modifyUserShift"),
				cache : false,
				type : "POST",
				success : function(response) {
					$('#shiftId').val(null);
					$('#shiftButton').html('Start Shift');
				},
				data : {
					'shiftId' : shiftId,
					'shiftStartTime' : shiftStartTime,
					'shiftEndTime' : shiftEndTime
				},
				error : function() {
					$('#shiftButton').html('Stop Shift');
				}

			});
		})

// $('.timepicker').timepicker();

/*
 * $('#timepicker1').timepicker(); $('#timepicker2').timepicker();
 * $('#timepicker3').timepicker(); $('#timepicker4').timepicker();
 * 
 * $('#timepicker2').timepicker({ minuteStep: 1, template: 'modal',
 * appendWidgetTo: 'body', showSeconds: true, showMeridian: false, defaultTime:
 * false }); $('#timepicker2').timepicker('showWidget');
 * 
 * $('#timepicker').timepicker('setTime', '12:45 AM');
 * $('.timepicker').timepicker();
 * 
 * $(function() { $('#datetimepicker3').datetimepicker({ pickDate: false }); });
 * 
 * 
 * 
 * $(function() { $('#datetimepicker5').datetimepicker({ language: 'en',
 * pick12HourFormat: true }); });
 * 
 * $('.timepicker-default').timepicker(); $().bfhtimepicker('toggle')
 */

/*
 * $(function() { $('#datetimepicker1').datetimepicker({ language: 'pt-BR' });
 * }); $('#datetimepicker2').datetimepicker();
 */

