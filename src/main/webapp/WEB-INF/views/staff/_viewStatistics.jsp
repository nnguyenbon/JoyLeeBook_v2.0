<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="flex-1 bg-gray-100 p-4">
  <!-- Dashboard Content -->
  <div class="flex-1 grid grid-cols-8 gap-6 px-6">
    <!-- Bar Chart - Total Interactions -->
    <div class="col-span-2 max-w-sm w-full bg-white rounded-lg shadow-sm md:p-4">
      <div class="flex justify-between border-gray-200 border-b pb-3">
        <dl>
          <dt class="text-base font-normal text-gray-500 pb-1">Total Interactions</dt>
          <dd class="leading-none text-3xl font-bold text-gray-900">
            <fmt:formatNumber value="${interactionStats.totalInteractions}" />
          </dd>
        </dl>
      </div>
      <div id="bar-chart"></div>
    </div>

    <!-- Statistics Overview -->
    <div class="col-span-6 grid grid-cols-6 gap-6">
      <!-- Series Group -->
      <div class="col-span-3 grid grid-cols-4 gap-4">
        <!-- Total Series -->
        <div class="col-span-4 border border-gray-300 rounded-lg bg-white flex items-center justify-between px-8 py-4 shadow-sm">
          <span class="text-2xl font-semibold text-gray-700">Total Series</span>
          <div class="flex items-center gap-3">
            <span class="text-4xl font-bold text-[#195DA9]">${dashboardStats.totalSeries}</span>
            <i class="fa-solid fa-book text-3xl text-[#195DA9]"></i>
          </div>
        </div>

        <!-- Active Users -->
        <div class="col-span-2 border border-gray-300 rounded-lg bg-white py-4 px-8 flex flex-col justify-between shadow-sm">
          <p class="text-gray-600 font-semibold">Active Users</p>
          <div class="flex items-center justify-between">
            <p class="text-green-500 text-2xl font-bold">${dashboardStats.activeUsers}</p>
            <i class="fa-solid fa-users text-green-500 text-3xl"></i>
          </div>
        </div>

        <!-- Authors -->
        <div class="col-span-2 border border-gray-300 rounded-lg bg-white py-4 px-8 flex flex-col justify-between items-start shadow-sm">
          <p class="text-gray-600 font-semibold">Authors</p>
          <div class="flex items-center justify-between w-full">
            <p class="text-blue-500 text-2xl font-bold">${dashboardStats.authors}</p>
            <i class="fa-solid fa-pen-nib text-blue-500 text-2xl"></i>
          </div>
        </div>

        <!-- Banned Users -->
        <div class="col-span-2 border border-gray-300 rounded-lg bg-white py-4 px-8 flex flex-col justify-between items-start shadow-sm">
          <p class="text-gray-600 font-semibold">Banned Users</p>
          <div class="flex items-center justify-between w-full">
            <p class="text-red-500 text-2xl font-bold">${dashboardStats.bannedUsers}</p>
            <i class="fa-solid fa-user-slash text-red-500 text-2xl"></i>
          </div>
        </div>

        <!-- Staffs -->
        <div class="col-span-2 border border-gray-300 rounded-lg bg-white py-4 px-8 flex flex-col justify-between items-start shadow-sm">
          <p class="text-gray-600 font-semibold">Staffs</p>
          <div class="flex items-center justify-between w-full">
            <p class="text-purple-500 text-2xl font-bold">${dashboardStats.staffs}</p>
            <i class="fa-solid fa-user-tie text-purple-500 text-2xl"></i>
          </div>
        </div>
      </div>

      <!-- Chapters Group -->
      <div class="col-span-3 grid grid-cols-4 gap-4">
        <!-- Total Chapters -->
        <div class="col-span-4 border border-gray-300 rounded-lg bg-white flex items-center justify-between px-8 py-4 shadow-sm">
          <span class="text-2xl font-semibold text-gray-700">Total Chapters</span>
          <div class="flex items-center gap-3">
            <span class="text-4xl font-bold text-[#195DA9]">${dashboardStats.totalChapters}</span>
            <i class="fa-solid fa-file-lines text-3xl text-[#195DA9]"></i>
          </div>
        </div>

        <!-- Pending Chapters -->
        <div class="col-span-2 border border-gray-300 rounded-lg bg-white py-4 px-8 flex flex-col justify-between shadow-sm">
          <p class="text-gray-600 font-semibold">Pending chapters</p>
          <div class="flex items-center justify-between">
            <p class="text-yellow-500 text-2xl font-bold">${dashboardStats.pendingChapters}</p>
            <i class="fa-regular fa-clock text-yellow-500 text-2xl"></i>
          </div>
        </div>

        <!-- Rejected -->
        <div class="col-span-2 border border-gray-300 rounded-lg bg-white py-4 px-8 flex flex-col justify-between shadow-sm">
          <p class="text-gray-600 font-semibold">Reject</p>
          <div class="flex items-center justify-between">
            <p class="text-red-500 text-2xl font-bold">${dashboardStats.rejectedChapters}</p>
            <i class="fa-solid fa-circle-xmark text-red-500 text-2xl"></i>
          </div>
        </div>

        <!-- Total Reports -->
        <div class="col-span-2 border border-gray-300 rounded-lg bg-white py-4 px-8 flex flex-col justify-between shadow-sm">
          <p class="text-gray-600 font-semibold">Total Reports</p>
          <div class="flex items-center justify-between">
            <p class="text-red-500 text-2xl font-bold">${dashboardStats.totalReports}</p>
            <i class="fa-solid fa-flag text-red-500 text-2xl"></i>
          </div>
        </div>

        <!-- Pending Reports -->
        <div class="col-span-2 border border-gray-300 rounded-lg bg-white py-4 px-8 flex flex-col justify-between shadow-sm">
          <p class="text-gray-600 font-semibold">Pending Reports</p>
          <div class="flex items-center justify-between">
            <p class="text-red-500 text-2xl font-bold">${dashboardStats.pendingReports}</p>
            <i class="fa-regular fa-clock text-red-500 text-2xl"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- Pie Chart - Favorite Genres -->
    <div class="col-span-2 max-w-sm w-full bg-white rounded-lg shadow-sm p-4 md:p-5">
      <div class="flex justify-between items-start w-full">
        <div class="flex-col items-center">
          <div class="flex items-center mb-2">
            <h5 class="text-xl font-bold leading-none text-gray-900">Favorite Genres</h5>
          </div>
        </div>
      </div>
      <div class="py-2" id="pie-chart"></div>
    </div>

    <!-- Area Chart - Users this week -->
    <div class="col-span-3 border border-gray-300 bg-white rounded-xl flex items-center justify-center">
      <div class="max-w-lg w-full p-4 md:p-6">
        <div class="flex justify-between">
          <div>
            <h5 class="leading-none text-3xl font-bold text-gray-900 pb-2">
              ${weeklyUserStats.thisWeekTotal}
            </h5>
            <p class="text-base font-normal text-gray-500">Users this month</p>
          </div>
          <div class="flex items-center px-2.5 py-0.5 text-base font-semibold
                        ${weeklyUserStats.growthRate >= 0 ? 'text-green-500' : 'text-red-500'}">
            <fmt:formatNumber value="${weeklyUserStats.growthRate}" maxFractionDigits="1" />%
            <c:choose>
              <c:when test="${weeklyUserStats.growthRate >= 0}">
                <svg class="w-3 h-3 ms-1" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 10 14">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                        stroke-width="2" d="M5 13V1m0 0L1 5m4-4 4 4" />
                </svg>
              </c:when>
              <c:otherwise>
                <svg class="w-3 h-3 ms-1" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 10 14">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                        stroke-width="2" d="M5 1v12m0 0l4-4m-4 4L1 9" />
                </svg>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
        <div id="area-chart"></div>

      </div>
    </div>

    <!-- Area Chart - Reports this week -->
    <div class="col-span-3 border border-gray-300 bg-white rounded-xl flex items-center justify-center">
      <div class="max-w-lg w-full p-4 md:p-6">
        <div class="flex justify-between">
          <div>
            <h5 class="leading-none text-3xl font-bold text-gray-900 pb-2">
              ${weeklyReportStats.thisWeekTotal}
            </h5>
            <p class="text-base font-normal text-gray-500">Reports this month</p>
          </div>
          <div class="flex items-center px-2.5 py-0.5 text-base font-semibold
                        ${weeklyReportStats.growthRate >= 0 ? 'text-green-500' : 'text-red-500'}">
            <fmt:formatNumber value="${weeklyReportStats.growthRate}" maxFractionDigits="1" />%
            <c:choose>
              <c:when test="${weeklyReportStats.growthRate >= 0}">
                <svg class="w-3 h-3 ms-1" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 10 14">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                        stroke-width="2" d="M5 13V1m0 0L1 5m4-4 4 4" />
                </svg>
              </c:when>
              <c:otherwise>
                <svg class="w-3 h-3 ms-1" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 10 14">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                        stroke-width="2" d="M5 1v12m0 0l4-4m-4 4L1 9" />
                </svg>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
        <div id="report-chart"></div>

      </div>
    </div>
  </div>
</div>

<script>
  // Bar Chart - Interactions
  const interactionOptions = {
    series: [{
      name: "Interactions",
      color: "#3B82F6",
      data: [
        ${interactionStats.likes},
        ${interactionStats.comments},
        ${interactionStats.votes},
        ${interactionStats.points},
        ${interactionStats.saves}
      ]
    }],
    chart: {
      sparkline: { enabled: false },
      type: "bar",
      width: 300,
      height: 220,
      toolbar: { show: false }
    },
    plotOptions: {
      bar: {
        horizontal: true,
        columnWidth: "100%",
        borderRadiusApplication: "end",
        borderRadius: 6,
        dataLabels: { position: "top" }
      }
    },
    legend: { show: false },
    dataLabels: {
      enabled: true,
      style: {
        colors: ['#333'],
        fontSize: '12px'
      }
    },
    tooltip: {
      shared: true,
      intersect: false,
      formatter: function (value) {
        return value + " interactions";
      }
    },
    xaxis: {
      labels: {
        show: true,
        style: {
          fontFamily: "Inter, sans-serif",
          cssClass: 'text-xs font-normal fill-gray-500'
        }
      },
      categories: ["Likes", "Comments", "Votes", "Points", "Saves"],
      axisTicks: { show: false },
      axisBorder: { show: false }
    },
    yaxis: {
      labels: {
        show: true,
        style: {
          fontFamily: "Inter, sans-serif",
          cssClass: 'text-xs font-normal fill-gray-500'
        }
      }
    },
    grid: {
      show: true,
      strokeDashArray: 4,
      padding: { left: 2, right: 2, top: 0 }
    },
    fill: { opacity: 1 }
  };

  if (document.getElementById("bar-chart") && typeof ApexCharts !== 'undefined') {
    const chart = new ApexCharts(document.getElementById("bar-chart"), interactionOptions);
    chart.render();
  }

  // Pie Chart - Genres
  const genreOptions = {
    series: [
      <c:forEach var="genre" items="${genreStatsList}" varStatus="status">
      ${genre.percentage}<c:if test="${!status.last}">,</c:if>
      </c:forEach>
    ],
    labels: [
      <c:forEach var="genre" items="${genreStatsList}" varStatus="status">
      "${genre.genreName}"<c:if test="${!status.last}">,</c:if>
      </c:forEach>
    ],
    colors: ["#3B82F6", "#F472B6", "#A855F7", "#22D3EE", "#F97316", "#10B981"],
    chart: {
      type: "pie",
      height: 300,
      width: "100%"
    },
    plotOptions: {
      pie: {
        dataLabels: { offset: 0 }
      }
    },
    dataLabels: {
      enabled: true,
      style: {
        fontFamily: "Inter, sans-serif",
        fontWeight: "bold",
        fontSize: "13px",
        colors: ["#fff"]
      },
      formatter: function (val, opts) {
        const name = opts.w.globals.labels[opts.seriesIndex];
        return name + "\n" + val.toFixed(1) + "%";
      }
    },
    legend: { show: false },
    markers: { radius: 1 },
    tooltip: {
      y: {
        formatter: (val) => val.toFixed(1) + "%"
      }
    }
  };

  if (document.getElementById("pie-chart") && typeof ApexCharts !== "undefined") {
    const genreChart = new ApexCharts(document.getElementById("pie-chart"), genreOptions);
    genreChart.render();
  }

  // Area Chart - Users
  const userAreaOptions = {
    chart: {
      height: "100%",
      maxWidth: "100%",
      type: "area",
      fontFamily: "Inter, sans-serif",
      toolbar: { show: false }
    },
    fill: {
      type: "gradient",
      gradient: {
        opacityFrom: 0.55,
        opacityTo: 0,
        shade: "#1C64F2",
        gradientToColors: ["#1C64F2"]
      }
    },
    dataLabels: { enabled: false },
    stroke: { width: 4 },
    series: [{
      name: "New users",
      data: [
        <c:forEach var="count" items="${weeklyUserStats.dailyCounts}" varStatus="status">
        ${count}<c:if test="${!status.last}">,</c:if>
        </c:forEach>
      ],
      color: "#1A56DB"
    }],
    xaxis: {
      categories: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      labels: { show: false },
      axisBorder: { show: false },
      axisTicks: { show: false }
    },
    yaxis: { show: false },
    grid: { show: false },
    tooltip: { enabled: true }
  };

  if (document.getElementById("area-chart") && typeof ApexCharts !== "undefined") {
    const userChart = new ApexCharts(document.getElementById("area-chart"), userAreaOptions);
    userChart.render();
  }

  // Area Chart - Reports
  const reportAreaOptions = {
    chart: {
      height: "100%",
      maxWidth: "100%",
      type: "area",
      fontFamily: "Inter, sans-serif",
      toolbar: { show: false }
    },
    fill: {
      type: "gradient",
      gradient: {
        opacityFrom: 0.55,
        opacityTo: 0,
        shade: "#ff0101ff",
        gradientToColors: ["#ff0000ff"]
      }
    },
    dataLabels: { enabled: false },
    stroke: { width: 4 },
    series: [{
      name: "New Reports",
      data: [
        <c:forEach var="count" items="${weeklyReportStats.dailyCounts}" varStatus="status">
        ${count}<c:if test="${!status.last}">,</c:if>
        </c:forEach>
      ],
      color: "#ff0000ff"
    }],
    xaxis: {
      categories: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      labels: { show: false },
      axisBorder: { show: false },
      axisTicks: { show: false }
    },
    yaxis: { show: false },
    grid: { show: false },
    tooltip: { enabled: true }
  };

  if (document.getElementById("report-chart") && typeof ApexCharts !== "undefined") {
    const reportChart = new ApexCharts(document.getElementById("report-chart"), reportAreaOptions);
    reportChart.render();
  }
</script>