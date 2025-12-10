<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="출석 코드 입력">
  <div class="flex flex-col flex-1 text-left w-full px-6 py-8">
    <div class="mb-6 w-full">
      <h1 class="text-2xl font-bold text-gray-900 tracking-tight">
        출석 코드 입력
      </h1>
      <div class="h-1 w-16 bg-primary-500 mt-2"></div>
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
            class="w-full rounded-lg border-2 border-gray-300 bg-white px-4 py-3 text-base text-gray-900 placeholder-gray-400 focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200 transition-colors duration-200"
            required
          />
        </div>

        <div class="flex items-center justify-between">
          <button
            type="submit"
            class="w-full rounded-lg bg-primary-500 py-3.5 text-white text-base font-bold hover:bg-primary-600 shadow-md hover:shadow-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary-300"
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
