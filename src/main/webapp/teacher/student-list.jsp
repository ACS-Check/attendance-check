<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<t:layout title="학생 관리">
  <div class="flex flex-col text-left w-full rounded-md shadow-md p-8 bg-white">
    <div class="mb-6 w-full flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-extrabold text-gray-900 tracking-tight">학생 목록</h1>
        <p class="text-sm text-gray-600 mt-1">등록된 학생 계정을 관리합니다.</p>
      </div>
      <a href="${pageContext.request.contextPath}/teacher/attendanceCode" class="text-sm text-primary-700 hover:underline">뒤로가기</a>
    </div>

    <!-- 검색/페이징 -->
    <form method="get" class="mb-4 flex items-center gap-2">
      <input type="text" name="q" value="${q}" placeholder="아이디/이름 검색" class="border border-gray-300 rounded px-3 py-2 text-sm w-64" />
      <select name="size" class="border border-gray-300 rounded px-2 py-2 text-sm">
        <option value="10" ${size==10? 'selected' : ''}>10</option>
        <option value="20" ${size==20? 'selected' : ''}>20</option>
        <option value="50" ${size==50? 'selected' : ''}>50</option>
      </select>
      <button class="rounded bg-primary-500 text-white px-3 py-2 text-sm">검색</button>
    </form>

    <div class="w-full">
      <c:if test="${empty students}">
        <div class="rounded-md border border-gray-200 bg-gray-50 px-4 py-3 text-gray-700">등록된 학생이 없습니다.</div>
      </c:if>
      <c:if test="${not empty students}">
        <table class="w-full text-sm border border-gray-200 rounded-md overflow-hidden">
          <thead>
            <tr class="bg-gray-100 text-left">
              <th class="px-4 py-2 font-semibold text-gray-700">ID</th>
              <th class="px-4 py-2 font-semibold text-gray-700">아이디</th>
              <th class="px-4 py-2 font-semibold text-gray-700">이름</th>
              <th class="px-4 py-2 font-semibold text-gray-700">권한</th>
              <th class="px-4 py-2 font-semibold text-gray-700">생성일</th>
              <th class="px-4 py-2 font-semibold text-gray-700">관리</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <c:forEach var="u" items="${students}">
              <tr class="hover:bg-gray-50">
                <td class="px-4 py-2 text-gray-900 font-medium">${u.userId}</td>
                <td class="px-4 py-2">${u.username}</td>
                <td class="px-4 py-2">${u.name}</td>
                <td class="px-4 py-2"><span class="inline-flex items-center rounded bg-primary-100 px-2 py-0.5 text-xs font-semibold text-primary-700">${u.role}</span></td>
                <td class="px-4 py-2 text-gray-600">${u.createdAt}</td>
                <td class="px-4 py-2">
                  <button class="text-xs font-semibold text-red-600 hover:underline js-delete" data-id="${u.userId}">삭제</button>
                  <button class="text-xs font-semibold text-primary-700 hover:underline js-reset" data-id="${u.userId}">비밀번호 초기화</button>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
        <div class="mt-4 flex items-center gap-2">
       <a class="px-3 py-1 border rounded text-sm ${page<=1? 'pointer-events-none text-gray-400' : ''}"
         href="?q=${fn:escapeXml(q)}&size=${size}&page=${page-1}">이전</a>
          <span class="text-sm text-gray-600">${page} / ${totalPages} (총 ${total}명)</span>
       <a class="px-3 py-1 border rounded text-sm ${page>=totalPages? 'pointer-events-none text-gray-400' : ''}"
         href="?q=${fn:escapeXml(q)}&size=${size}&page=${page+1}">다음</a>
        </div>
      </c:if>
    </div>
  </div>

  <div id="toast" class="fixed right-4 bottom-4 hidden bg-black/80 text-white text-sm px-3 py-2 rounded"></div>
  <script>
    function showToast(msg) {
      const t = document.getElementById('toast');
      t.textContent = msg; t.classList.remove('hidden');
      setTimeout(() => t.classList.add('hidden'), 2500);
    }
    document.addEventListener('DOMContentLoaded', () => {
      document.querySelectorAll('.js-delete').forEach(btn => {
        btn.addEventListener('click', async () => {
          const id = btn.getAttribute('data-id');
          if (!confirm('삭제하시겠습니까? (ID=' + id + ')')) return;
          try {
            const resp = await fetch('${pageContext.request.contextPath}/teacher/students/delete', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: 'id=' + encodeURIComponent(id) });
            const data = await resp.json();
            if (resp.ok && data.success) {
              const tr = btn.closest('tr'); tr.parentNode.removeChild(tr);
              if (document.querySelectorAll('tbody tr').length === 0) {
                const container = document.querySelector('.w-full');
                container.innerHTML = '<div class="rounded-md border border-gray-200 bg-gray-50 px-4 py-3 text-gray-700">등록된 학생이 없습니다.</div>';
              }
              showToast('삭제 완료');
            } else { alert('처리에 실패했습니다. 다시 시도하세요.'); }
          } catch (e) { console.error(e); alert('서버 오류가 발생했습니다.'); }
        });
      });
      document.querySelectorAll('.js-reset').forEach(btn => {
        btn.addEventListener('click', async () => {
          const id = btn.getAttribute('data-id');
          if (!confirm('해당 사용자의 비밀번호를 임시 비밀번호로 초기화하시겠습니까?')) return;
          try {
            const resp = await fetch('${pageContext.request.contextPath}/teacher/students/resetPassword', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: 'id=' + encodeURIComponent(id) });
            const data = await resp.json();
            if (data.success) {
              try { await navigator.clipboard.writeText(data.tempPassword); } catch (e) {}
              showToast('임시 비밀번호가 생성되었습니다. (복사됨)');
              alert('임시 비밀번호: ' + data.tempPassword + '\n(클립보드에 복사되었습니다)');
            } else { alert('초기화 실패했습니다. 다시 시도하세요.'); }
          } catch (e) { console.error(e); alert('서버 오류가 발생했습니다.'); }
        });
      });
    });
  </script>
</t:layout>
