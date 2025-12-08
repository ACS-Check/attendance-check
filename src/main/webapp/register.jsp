<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:layout title="회원가입">
<style>
  .role-button {
    flex: 1;
    padding: 0.25rem 0.75rem;
    border-radius: 0.375rem;
    text-align: center;
    cursor: pointer;
  }
  .role-button.active {
    background-color: white;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    color: #1f2937;
  }
  .role-button.inactive {
    background-color: transparent;
    color: #6b7280;
  }
</style>
  <div class="flex flex-col text-left w-full max-w-[550px] rounded-md shadow-md p-8 bg-white m-auto">
    <div class="mb-6 w-full">
      <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight">
        회원가입
      </h1>
    </div>

    <div class="w-full">
  <form class="space-y-6" method="post" action="${pageContext.request.contextPath}/register">
        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="username" class="block text-sm font-semibold text-gray-800">
              아이디
            </label>
          </div>
          <div class="flex gap-2">
            <input
              id="username"
              name="username"
              type="text"
              placeholder="아이디를 입력하세요"
              class="flex-1 rounded-md border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder-gray-400 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200"
              required
            />
            <button
              type="button"
              id="checkUsernameBtn"
              class="px-4 py-3 bg-gray-600 text-white rounded-md hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-300 whitespace-nowrap"
            >
              중복 체크
            </button>
          </div>
          <div id="usernameCheckMessage" class="text-sm mt-1"></div>
        </div>

        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="password" class="block text-sm font-semibold text-gray-800">
              비밀번호
            </label>
          </div>
          <input
            id="password"
            name="password"
            type="password"
            placeholder="비밀번호를 입력하세요"
            class="w-full rounded-md border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder-gray-400 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200"
            required
          />
        </div>

        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="name" class="block text-sm font-semibold text-gray-800">
              이름
            </label>
          </div>
          <input
            id="name"
            name="name"
            type="text"
            placeholder="이름을 입력하세요"
            class="w-full rounded-md border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder-gray-400 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200"
            required
          />
        </div>

        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="role" class="block text-sm font-semibold text-gray-800">
              역할
            </label>
          </div>
          

          <div class="inline-flex rounded-md bg-gray-100 p-1 w-full">
            <input
              type="radio"
              id="role-student"
              name="role"
              value="student"
              class="hidden"
              onclick="
                document.getElementById('role-student-label').classList.add('active');
                document.getElementById('role-student-label').classList.remove('inactive');
                document.getElementById('role-admin-label').classList.add('inactive');
                document.getElementById('role-admin-label').classList.remove('active');
              "
              checked
            />
            <label
              id="role-student-label"
              for="role-student"
              class="role-button active"
            >
              학생
            </label>

            <input
              type="radio"
              id="role-admin"
              name="role"
              value="admin"
              class="hidden"
              onclick="
                document.getElementById('role-admin-label').classList.add('active');
                document.getElementById('role-admin-label').classList.remove('inactive');
                document.getElementById('role-student-label').classList.add('inactive');
                document.getElementById('role-student-label').classList.remove('active');
              "
            />
            <label
              id="role-admin-label"
              for="role-admin"
              class="role-button inactive"
            >
              강사
            </label>
          </div>
        </div>

        <div class="flex items-center justify-between">
          <button
            type="submit"
            class="w-full rounded-md bg-primary-600 py-3 text-white font-semibold shadow-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-300"
          >
            가입
          </button>
          <a
            href="${pageContext.request.contextPath}/login"
            class="ml-4 w-full text-center rounded-md bg-gray-100 py-3 text-gray-700 font-semibold shadow-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-300"
          >
            로그인
          </a>
        </div>
      </form>
    </div>
  </div>

  <script>
    const contextPath = '${pageContext.request.contextPath}';
    let usernameChecked = false;
    let lastCheckedUsername = '';

    document.getElementById('username').addEventListener('input', function() {
      // 아이디 입력값이 변경되면 중복 체크 상태 리셋
      if (this.value !== lastCheckedUsername) {
        usernameChecked = false;
        document.getElementById('usernameCheckMessage').textContent = '';
      }
    });

    document.getElementById('checkUsernameBtn').addEventListener('click', function() {
      const username = document.getElementById('username').value.trim();
      const messageElement = document.getElementById('usernameCheckMessage');
      
      if (!username) {
        messageElement.textContent = '아이디를 입력하세요.';
        messageElement.className = 'text-sm mt-1 text-red-600';
        return;
      }

      // 중복 체크 중 표시
      messageElement.textContent = '확인 중...';
      messageElement.className = 'text-sm mt-1 text-gray-600';

      const apiUrl = contextPath + '/api/check-username?username=' + encodeURIComponent(username);

      fetch(apiUrl)
        .then(response => {
          console.log('Response status:', response.status);
          if (!response.ok) {
            throw new Error('HTTP error! status: ' + response.status);
          }
          return response.json();
        })
        .then(data => {
          messageElement.textContent = data.message;
          
          if (data.available) {
            messageElement.className = 'text-sm mt-1 text-green-600';
            usernameChecked = true;
            lastCheckedUsername = username;
          } else {
            messageElement.className = 'text-sm mt-1 text-red-600';
            usernameChecked = false;
          }
        })
        .catch(error => {
          console.error('중복 체크 오류:', error);
          messageElement.textContent = '중복 체크 중 오류가 발생했습니다.';
          messageElement.className = 'text-sm mt-1 text-red-600';
          usernameChecked = false;
        });
    });

    // 폼 제출 시 중복 체크 및 유효성 검사
    document.querySelector('form').addEventListener('submit', function(e) {
      const username = document.getElementById('username').value.trim();
      const password = document.getElementById('password').value.trim();
      const name = document.getElementById('name').value.trim();
      
      // 빈 값 체크
      if (!username || !password || !name) {
        e.preventDefault();
        alert('모든 필드를 입력해주세요.');
        return false;
      }
      
      // 중복 체크 확인
      if (!usernameChecked || username !== lastCheckedUsername) {
        e.preventDefault();
        alert('아이디 중복 체크를 먼저 진행해주세요.');
        return false;
      }
    });
  </script>
</t:layout>
