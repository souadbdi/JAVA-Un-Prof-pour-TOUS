<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<link href="<c:url value="/resources/bootstrap/css/bootstrap.css"/>"
	rel="stylesheet" />
<!-- INCLURE UN CSS PERSO ICI -->

<title>UPPT | Complétez votre profil</title>
</head>
<body>


	<!--  HEADER INCLUSION  -->


	<div class="container">
		<header class="row">
		<div class="col-lg-12">
			<%@ include file="../header.jsp"%>
		</div>
		</header>
	</div>

<hr>
	<!-- Formulaire -->

	<div class="container">
		<div class="row">
			<div class="col-lg-offset-3 col-lg-6">
				<%@ include file="../forms/user-complement-infos-form.jsp"%>
			</div>

		</div>
	</div>


	<!--  FOOTER INCLUSION  -->

	<div class="jumbotron">
		<div class="container">
			<footer class="row">
			<div class="col-lg-12">
				<%@ include file="../footer.jsp"%>
			</div>
			</footer>
		</div>
	</div>
</body>
</html>