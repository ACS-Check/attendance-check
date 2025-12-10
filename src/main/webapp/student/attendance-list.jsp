<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:layout title="나의 출석 기록">
  <div class="flex flex-col flex-1 w-full px-6 py-8">
    <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 mb-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 mb-1">나의 출석 기록</h1>
        <p class="text-sm text-gray-600">월을 선택하면 해당 월의 출석/지각 내역을 불러옵니다.</p>
        <div class="h-1 w-16 bg-primary-500 mt-2"></div>
      </div>
      <div class="flex items-center gap-2">
    <label for="month" class="text-sm font-semibold text-gray-700">월 선택</label>
  <input id="month" type="month" value="${initialMonth}" class="rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-primary-500 focus:ring-2 focus:ring-primary-200" />
  <button type="button" id="reloadBtn" class="rounded-lg bg-primary-500 text-white px-6 py-2.5 text-sm font-bold hover:bg-primary-600 shadow-md transition-all duration-200">조회</button>
      </div>
    </div>

    <div class="mb-4 flex flex-wrap gap-4 text-sm">
      <div class="flex items-center gap-2 bg-green-50 px-4 py-2 rounded-md border-l-4 border-green-500"><span class="font-semibold text-green-700">총 출석 일수:</span><span id="presentCount" class="font-bold text-green-600">0</span></div>
      <div class="flex items-center gap-2 bg-yellow-50 px-4 py-2 rounded-md border-l-4 border-yellow-500"><span class="font-semibold text-yellow-700">총 지각 일수:</span><span id="lateCount" class="font-bold text-yellow-600">0</span></div>
    </div>

    <div class="overflow-x-auto rounded-lg shadow-md">
      <table class="min-w-full border text-sm" id="attendanceTable">
        <thead>
          <tr class="bg-primary-500 text-white text-left">
            <th class="px-3 py-2 border">날짜</th>
            <th class="px-3 py-2 border">시간</th>
            <th class="px-3 py-2 border">상태</th>
          </tr>
        </thead>
        <tbody id="attBody">
        <c:choose>
          <c:when test="${not empty initialList}">
            <c:forEach var="r" items="${initialList}">
              <tr>
                <td class="px-3 py-2 border">${r.attendDate}</td>
                <td class="px-3 py-2 border">${r.attendTime}</td>
                <td class="px-3 py-2 border">${r.status}</td>
              </tr>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <tr><td colspan="3" class="px-3 py-4 text-center text-gray-500">데이터가 없습니다.</td></tr>
          </c:otherwise>
        </c:choose>
        </tbody>
      </table>
    </div>
  </div>

  <script>
    const monthEl = document.getElementById('month');
    const attBody = document.getElementById('attBody');
    const presentCountEl = document.getElementById('presentCount');
    const lateCountEl = document.getElementById('lateCount');
    const reloadBtn = document.getElementById('reloadBtn');

    // 기본값: 오늘 월
    // 서버 제공 초기 월 있으면 반영
    const initialMonth = '${initialMonth}';
    if (initialMonth && /^\d{4}-\d{2}$/.test(initialMonth)) {
      monthEl.value = initialMonth;
    } else {
      // 로컬 타임존 기준 현재 월 (UTC 문제 회피)
      const d = new Date();
      const yyyy = d.getFullYear();
      const mm = String(d.getMonth() + 1).padStart(2, '0');
      monthEl.value = `${yyyy}-${mm}`;
    }

    async function loadAttendance() {
      console.debug('[attendance] loadAttendance clicked');
      const month = monthEl.value;
      if (!month) return;
      try {
  // 컨텍스트 패스: JSTL/EL 사용 (스크립틀릿 금지 환경 대응)
  const ctx = '${pageContext.request.contextPath}';
        attBody.innerHTML = '<tr><td colspan="3" class="px-3 py-4 text-center text-gray-500">불러오는 중...</td></tr>';
        const res = await fetch(ctx + '/attend/list?month=' + encodeURIComponent(month), { credentials: 'same-origin' });
        let data;
        if (!res.ok) {
          const txt = await res.text();
          attBody.innerHTML = '<tr><td colspan="3" class="px-3 py-4 text-center text-red-600">불러오기 실패 (' + res.status + ')<br/><small>' + (txt || '').substring(0,120) + '</small></td></tr>';
          return;
        }
        try {
          data = await res.json();
        } catch (jsonErr) {
          const txt = await res.text();
          attBody.innerHTML = '<tr><td colspan="3" class="px-3 py-4 text-center text-red-600">응답 파싱 오류<br/><small>' + (txt || '').substring(0,120) + '</small></td></tr>';
          return;
        }
        if (!Array.isArray(data) || data.length === 0) {
          attBody.innerHTML = '<tr><td colspan="3" class="px-3 py-4 text-center text-gray-500">해당 월 출석 기록이 없습니다.</td></tr>';
          presentCountEl.textContent = 0;
          lateCountEl.textContent = 0;
          return;
        }
        let present = 0, late = 0;
        attBody.innerHTML = data.map(r => {
          if (r.status === '출석') present++; else if (r.status === '지각') late++;
          return '<tr>' +
              '<td class="px-3 py-2 border">' + r.date + '</td>' +
              '<td class="px-3 py-2 border">' + r.time + '</td>' +
              '<td class="px-3 py-2 border">' + r.status + '</td>' +
            '</tr>';
        }).join('');
        presentCountEl.textContent = present;
        lateCountEl.textContent = late;
      } catch (e) {
        console.error(e);
        attBody.innerHTML = '<tr><td colspan="3" class="px-3 py-4 text-center text-red-600">오류가 발생했습니다.</td></tr>';
      }
    }

    document.addEventListener('DOMContentLoaded', () => {
      // 노출 보장: 전역 참조
  window.loadAttendance = loadAttendance;
  if (reloadBtn) reloadBtn.addEventListener('click', (e) => { e.preventDefault(); console.log('click'); loadAttendance(); });
      if (monthEl) monthEl.addEventListener('change', () => loadAttendance());
      loadAttendance();
    });
  </script>
</t:layout>
