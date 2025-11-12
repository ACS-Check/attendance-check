<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="출석 코드 입력">
  <div class="flex flex-col text-left w-[550px] rounded-md shadow-md p-8 bg-white">
    <div class="mb-6 w-full">
      <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight">
        출석 코드 입력
      </h1>
    </div>

    <div class="w-full">
      <form id="attendanceForm" class="space-y-6">
        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="attendanceCode" class="block text-sm font-semibold text-gray-800">
              출석 코드
            </label>
          </div>
          <input
            id="attendanceCode"
            name="code"
            type="text"
            placeholder="출석 코드를 입력하세요"
            class="w-full rounded-md border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder-gray-400 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200"
            required
          />
        </div>

        <div class="flex items-center justify-between">
          <button
            type="submit"
            class="w-full rounded-md bg-primary-600 py-3 text-white font-semibold shadow-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-300"
          >
            출석 확인
          </button>
        </div>
      </form>
    </div>
  </div>

  <script>
    document.getElementById('attendanceForm').addEventListener('submit', async function(event) {
      event.preventDefault();

      const attendanceCode = document.getElementById('attendanceCode').value;

      try {
        const ctx = '${pageContext.request.contextPath}';
        const response = await fetch(ctx + '/attend/mark', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          body: new URLSearchParams({ code: attendanceCode }),
        });

        if (response.ok) {
          const result = await response.json();
          alert(`출석 완료!\n출석 ID: ${result.attendId}\n사용자 ID: ${result.userId}\n날짜: ${result.date}\n시간: ${result.time}\n상태: ${result.status}\n코드 ID: ${result.codeId}`);
        } else {
          const errorMessage = await response.text();
          alert(`출석 실패: ${errorMessage}`);
        }
      } catch (error) {
        console.error('Error:', error);
        alert('오류가 발생했습니다. 다시 시도해주세요.');
      }
    });
  </script>
</t:layout>
