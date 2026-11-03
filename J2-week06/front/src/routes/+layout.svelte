<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	async function logout() {
		await rq.getClient().DELETE('/api/v1/members/logout');
		rq.setLogout();

		rq.goto('/');
	}

	$effect(() => {
		rq.initAuth();
	});
</script>

<header>
	<nav>
		<a href="/">메인</a>
		{#if rq.isLogin()}
			<button type="button" on:click|preventDefault={logout}>로그아웃</button>
			<a href="/member/me">{rq.member.nickname}님의 정보</a>
			<a href="/surl/list">SURL 목록</a>
			<a href="/surl/add">SURL 등록</a>
		{/if}
		{#if rq.isLogout()}
			<a href="/member/login">로그인</a>
			<a href="/member/join">회원가입</a>
		{/if}

		<a href="/todo">할일 리스트</a>
	</nav>
</header>

<slot></slot>
