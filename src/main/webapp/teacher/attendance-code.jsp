<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="출석코드 생성">
  <div class="flex flex-col text-left w-full max-w-[550px] rounded-md shadow-md p-8 bg-white">
    <div class="mb-6 w-full">
      <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight">
        출석코드 생성
      </h1>
    </div>

    <div class="w-full" id="codeFormContainer">
      <form class="space-y-6" id="codeForm">
        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="validMinutes" class="block text-sm font-semibold text-gray-800">
              유효 시간 (분)
            </label>
          </div>
          <select
            id="validMinutes"
            name="validMinutes"
            class="w-full rounded-md border border-gray-300 bg-white px-4 py-3 text-gray-900 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200"
            required
          >
            <option value="1">1분</option>
            <option value="3">3분</option>
            <option value="5" selected>5분</option>
            <option value="10">10분</option>
            <option value="15">15분</option>
            <option value="20">20분</option>
          </select>
        </div>

        <div class="flex items-center justify-between">
          <button
            type="button"
            id="generateCodeButton"
            class="w-full rounded-md bg-primary-600 py-3 text-white font-semibold shadow-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-300"
          >
            코드 생성
          </button>
        </div>
      </form>
    </div>

    <!-- Display Generated Code -->
    <div class="mt-8" id="generatedCodeContainer" style="display: none;">
      <div class="text-center">
        <p id="generatedCode" class="text-4xl font-extrabold text-primary-600 mt-4"></p>
        <p id="remainingTime" class="text-lg font-semibold text-gray-800 mt-2"></p>
      </div>
    </div>
    <div class="mt-8 flex gap-6">
      <a href="${pageContext.request.contextPath}/teacher/studentList" class="text-sm text-primary-700 hover:underline">학생 관리</a>
      <a href="${pageContext.request.contextPath}/teacher/attendanceList" class="text-sm text-primary-700 hover:underline">출석 요약</a>
    </div>
  </div>

  <script>
    let countdownInterval;

    document.getElementById('generateCodeButton').addEventListener('click', async () => {
      const validMinutes = parseInt(document.getElementById('validMinutes').value, 10);
      if (isNaN(validMinutes) || validMinutes <= 0) {
        alert('유효한 시간을 선택해주세요.');
        return;
      }

      try {
        const ctx = '${pageContext.request.contextPath}';
        const response = await fetch(ctx + '/teacher/code', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          body: new URLSearchParams({ validMinutes: String(validMinutes) }),
        });

        if (response.ok) {
          const data = await response.json();
          document.getElementById('generatedCode').textContent = data.code;
          document.getElementById('generatedCodeContainer').style.display = 'block';
          document.getElementById('codeFormContainer').style.display = 'none';

          let remainingSeconds = validMinutes * 60; // 유저가 선택한 분을 기준으로 남은 시간 계산

          const updateCountdown = () => {
            if (remainingSeconds <= 0) {
              clearInterval(countdownInterval);
              document.getElementById('generatedCodeContainer').style.display = 'none';
              document.getElementById('codeFormContainer').style.display = 'block';
            } else {
              const minutes = Math.floor(remainingSeconds / 60);
              const seconds = remainingSeconds % 60;
              document.getElementById('remainingTime').textContent = "(" + minutes + ':' + (seconds < 10 ? '0' : '') + seconds + ")";
              remainingSeconds--;
            }
          };

          updateCountdown(); // 즉시 실행
          countdownInterval = setInterval(updateCountdown, 1000); // 1초마다 실행
        } else {
          alert('코드 생성에 실패했습니다. 다시 시도해주세요.');
        }
      } catch (error) {
        console.error('Error:', error);
        alert('서버와의 연결에 문제가 발생했습니다.');
      }
    });
  </script>
</t:layout>
