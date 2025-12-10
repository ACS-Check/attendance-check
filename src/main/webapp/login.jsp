<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:layout title="출석 관리 시스템 - 로그인">
  <div class="flex flex-col items-center w-full max-w-md relative">
    <div class="relative flex items-center justify-center gap-4 bg-gradient-to-r from-gray-950 via-black to-gray-950 px-8 py-4 rounded-t-2xl shadow-[0_0_50px_rgba(255,153,0,0.5)] border-4 border-b-0 border-primary-500 overflow-hidden z-10"> 
      <div class="absolute inset-0 bg-gradient-to-r from-primary-500/30 via-primary-400/40 to-primary-500/30 animate-pulse"></div>
      <div class="relative bg-white p-2 rounded-lg shadow-[0_0_30px_rgba(255,153,0,0.8)] animate-pulse">
        <img src="${pageContext.request.contextPath}/static/icon-aws.svg" alt="AWS Cloud School" class="h-10 w-10">
      </div>
      <h2 class="relative text-2xl font-black text-primary-300 tracking-wide animate-pulse brightness-150" style="text-shadow: 0 0 30px rgba(255,200,100,1), 0 0 50px rgba(255,180,80,0.9), 0 0 70px rgba(255,153,0,0.7), 0 0 90px rgba(255,153,0,0.5);">CI/CD 테스트</h2>
      <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-primary-400 to-transparent shadow-[0_0_15px_rgba(255,153,0,0.8)] animate-pulse"></div>
      <div class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-12 bg-gradient-to-b from-transparent via-primary-400 to-transparent shadow-[0_0_15px_rgba(255,153,0,0.8)] animate-pulse"></div>
      <div class="absolute right-0 top-1/2 -translate-y-1/2 w-1 h-12 bg-gradient-to-b from-transparent via-primary-400 to-transparent shadow-[0_0_15px_rgba(255,153,0,0.8)] animate-pulse"></div>
    </div>
    <div class="flex justify-between w-full px-28 -mt-px z-0">
      <div class="w-2 h-10 bg-gradient-to-b from-primary-500 to-primary-600 shadow-[0_0_20px_rgba(255,153,0,0.7)] rounded-b-sm"></div>
      <div class="w-2 h-10 bg-gradient-to-b from-primary-500 to-primary-600 shadow-[0_0_20px_rgba(255,153,0,0.7)] rounded-b-sm"></div>
    </div>
    
    <div class="flex flex-col text-left w-full px-8 py-10 bg-white rounded-b-2xl shadow-[0_20px_50px_rgba(0,0,0,0.3),0_30px_80px_rgba(0,0,0,0.2),0_40px_100px_rgba(255,153,0,0.15),inset_0_-2px_10px_rgba(0,0,0,0.1)] border-x-4 border-primary-500 relative overflow-hidden -mt-px">
      <div class="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-primary-400 via-primary-500 to-primary-600 animate-pulse"></div>
      
      <div class="mb-6 w-full">
      <h1 class="text-3xl font-bold text-gray-900 tracking-tight">
        로그인
      </h1>
      <div class="h-1 w-16 bg-primary-500 mt-3"></div>
    </div>

    <div class="w-full">
  <form class="space-y-6" method="post" action="${pageContext.request.contextPath}/login">
        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="userId" class="block text-sm font-semibold text-gray-800">
              아이디
            </label>
          </div>
          <input
            id="userId"
            name="username"
            type="text"
            placeholder="아이디를 입력하세요"
            class="w-full rounded-lg border-2 border-gray-300 bg-gradient-to-br from-white to-gray-50 px-4 py-3.5 text-gray-900 placeholder-gray-400 shadow-md focus:border-primary-500 focus:outline-none focus:ring-4 focus:ring-primary-300 focus:shadow-xl focus:scale-[1.02] transition-all duration-300"
            required
          />
        </div>

        <div class="space-y-1">
          <div class="mb-1 flex items-center justify-between">
            <label for="password" class="block text-sm font-bold text-gray-900">
              비밀번호
            </label>
          </div>
          <input
            id="password"
            name="password"
            type="password"
            placeholder="비밀번호를 입력하세요"
            class="w-full rounded-lg border-2 border-gray-300 bg-gradient-to-br from-white to-gray-50 px-4 py-3.5 text-gray-900 placeholder-gray-400 shadow-md focus:border-primary-500 focus:outline-none focus:ring-4 focus:ring-primary-300 focus:shadow-xl focus:scale-[1.02] transition-all duration-300"
            required
          />
        </div>

        <div class="flex items-center justify-between gap-4">
          <button
            type="submit"
            class="w-full rounded-lg bg-primary-500 py-3.5 text-white font-bold shadow-lg hover:bg-primary-600 hover:shadow-xl transform hover:scale-[1.02] transition-all duration-200 focus:outline-none focus:ring-4 focus:ring-primary-200"
          >
            로그인
          </button>
          <a
            href="${pageContext.request.contextPath}/register"
            class="w-full text-center rounded-lg bg-gray-700 py-3.5 text-white font-bold hover:bg-gray-800 shadow-md hover:shadow-lg transition-all duration-200 focus:outline-none"
          >
            회원가입
          </a>
        </div>
      </form>
    </div>
  </div>
</t:layout>
