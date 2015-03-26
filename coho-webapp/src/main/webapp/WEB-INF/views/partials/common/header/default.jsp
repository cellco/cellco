<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="<core:url value = '/' />"> - | COHO | -</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right">
				<li><a href="dashboard.html"> <span class="glyphicon glyphicon-home"
						aria-hidden="false"></span> Dashboard
				</a></li>
				<li role="presentation" class="dropdown"><a
					class="dropdown-toggle" data-toggle="dropdown" href="#"
					role="button" aria-expanded="false"> Leave Manager <span
						class="caret"></span>
				</a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="apply-leave.html"> <span class="glyphicon glyphicon-pencil"
								aria-hidden="false"></span> Apply Leave
						</a></li>
						<li><a href="my-leave-list.html"> <span
								class="glyphicon glyphicon-th-list" aria-hidden="false"></span>
								My Leave List
						</a></li>
						<li><a href="add-leave-type.html"> <span class="glyphicon glyphicon-plus"
								aria-hidden="false"></span> Add Leave Type
						</a></li>
						<li><a href="leave-type-list.html"> <span
								class="glyphicon glyphicon-th-list" aria-hidden="false"></span>
								Leave Type List
						</a></li>
						<li><a href="leaves-list.html"> <span
								class="glyphicon glyphicon-th-list" aria-hidden="false"></span>
								Leaves List
						</a></li>
						<li><a href="pending-approvals.html"> <span 
						        class="badge">5</span>
								Pending Approvals
						</a></li>
						<li><a href="leave-planner.html"> <span
                                class="glyphicon glyphicon-calendar" aria-hidden="false"></span>
                                Leave Planner
                        </a></li>
					</ul></li>
				<li role="presentation" class="dropdown"><a
					class="dropdown-toggle" data-toggle="dropdown" href="#"
					role="button" aria-expanded="false"> User Manager <span
						class="caret"></span>
				</a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="add-user.html"> <span class="glyphicon glyphicon-plus"
								aria-hidden="false"></span> Add User
						</a></li>
						<li><a href="user-list.html"> <span
								class="glyphicon glyphicon-th-list" aria-hidden="false"></span>
								User List
						</a></li>
						<li><a href="tracker.html"> <span class="glyphicon glyphicon-road"
								aria-hidden="false"></span> Tracker
						</a></li>
					</ul></li>
				<li role="presentation" class="dropdown"><a
					class="dropdown-toggle" data-toggle="dropdown" href="#"
					role="button" aria-expanded="false"> Reports <span
						class="caret"></span>
				</a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="user-statistics.html"> <span
								class="glyphicon glyphicon-th-list" aria-hidden="false"></span>
								User Statistics
						</a></li>
						<li><a href="overall-statistics.html"> <span class="glyphicon glyphicon-th"
								aria-hidden="false"></span> Overall Statistics
						</a></li>
					</ul></li>
				<li role="presentation" class="dropdown"><a
					class="dropdown-toggle" data-toggle="dropdown" href="#"
					role="button" aria-expanded="false"> User name <span
						class="caret"></span>
				</a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="profile.html"> <span class="glyphicon glyphicon-user"
								aria-hidden="false"></span> Profile
						</a></li>
						<li><a href="#"> <span class="glyphicon glyphicon-off"
								aria-hidden="false"></span> Log Out
						</a></li>
					</ul></li>
			</ul>
		</div>
	</div>
</nav>