<script lang="ts">
	type Todo = {
		id: number;
		body: string;
		done: boolean;
		edting: boolean;
	};

	let todosLastId = 0;
	const todos = $state<Todo[]>([]);
	const todosCount = $derived(todos.length);
	const doneCount = $derived(todos.filter((todo) => todo.done).length);
	const donePercent = $derived(todosCount == 0 ? 0 : Math.round((doneCount / todosCount) * 100));

	function addTodo(this: HTMLFormElement) {
		const form: HTMLFormElement = this;

		form.body.value = form.body.value.trim();

		if (form.body.value.length === 0) {
			alert('할일을 입력해주세요.');
			form.body.focus();

			return;
		}

		let body = $state(form.body.value);
		let done = $state(false);
		let edting = $state(false);

		const todo = {
			id: ++todosLastId,
			get body() {
				return body;
			},
			set body(value: string) {
				body = value;
			},
			get done() {
				return done;
			},
			set done(value: boolean) {
				done = value;
			},
			get edting() {
				return edting;
			},
			set edting(value: boolean) {
				edting = value;
			}
		};

		todos.push(todo);

		form.body.value = '';
		form.body.focus();
	}

	function deleteTodo(todo: Todo) {
		todos.splice(todos.indexOf(todo), 1);
	}

	function modifyTodo(form: HTMLFormElement, todo: Todo) {
		form.body.value = form.body.value.trim();

		if (form.body.value.length === 0) {
			alert('할일을 입력해주세요.');
			form.body.focus();

			return;
		}

		todo.body = form.body.value;
		todo.edting = false;
	}
</script>

<h1>할 일 앱</h1>

<h2>할 일 추가</h2>
<form on:submit|preventDefault={addTodo}>
	<input type="text" name="body" placeholder="할일을 입력해주세요." autocomplete="off" />
	<button type="submit">추가</button>
</form>

<h2>할 일 리스트({doneCount}/{todosCount})(진행률 : {donePercent}%)</h2>

<ul>
	{#each todos as todo (todo.id)}
		<li style="display:flex; gap:5px;">
			<input type="checkbox" bind:checked={todo.done} />

			<span>{todo.id}</span>

			{#if todo.edting}
				<form
					style="display:flex; gap:5px;"
					on:submit|preventDefault={(event) => modifyTodo(event.target as HTMLFormElement, todo)}
				>
					<input
						type="text"
						name="body"
						placeholder="할일을 입력해주세요."
						autocomplete="off"
						value={todo.body}
					/>
					<button type="submit">적용</button>
					<button type="button" on:click|preventDefault={() => (todo.edting = false)}
						>수정취소</button
					>
				</form>
			{:else}
				{todo.body}
				<button type="button" on:click|preventDefault={() => deleteTodo(todo)}>삭제</button>
				<button type="button" on:click|preventDefault={() => (todo.edting = true)}>수정</button>
			{/if}
		</li>
	{/each}
</ul>
