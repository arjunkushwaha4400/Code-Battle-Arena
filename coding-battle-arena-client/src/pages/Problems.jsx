import React, { useState, useEffect } from 'react';
import { apiService } from '../services/apiService';
import Loading from '../components/Loading';

function Problems() {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [difficulty, setDifficulty] = useState('ALL');
  const [error, setError] = useState(null);
  const [selectedProblem, setSelectedProblem] = useState(null);

  useEffect(() => {
    loadProblems();
  }, [page, difficulty]);

  const loadProblems = async () => {
    setLoading(true);
    setError(null);
    try {
      let response;
      if (difficulty === 'ALL') {
        response = await apiService.getProblems(page, 10);
      } else {
        response = await apiService.getProblemsByDifficulty(difficulty, page, 10);
      }
      setProblems(response.content);
      setTotalPages(response.totalPages);
    } catch (error) {
      setError(error.message || 'Failed to load problems');
    } finally {
      setLoading(false);
    }
  };

  const getDifficultyBadge = (diff) => {
    switch (diff) {
      case 'EASY':
        return <span className="badge bg-success">Easy</span>;
      case 'MEDIUM':
        return <span className="badge bg-warning text-dark">Medium</span>;
      case 'HARD':
        return <span className="badge bg-danger">Hard</span>;
      default:
        return <span className="badge bg-secondary">{diff}</span>;
    }
  };

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>📝 Practice Problems</h2>
        <div>
          <select
            className="form-select"
            value={difficulty}
            onChange={(e) => {
              setDifficulty(e.target.value);
              setPage(0);
            }}
          >
            <option value="ALL">All Difficulties</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row">
        {/* Problems List */}
        <div className={selectedProblem ? 'col-md-5' : 'col-12'}>
          {loading ? (
            <Loading message="Loading problems..." />
          ) : (
            <div className="card">
              <div className="list-group list-group-flush">
                {problems.map((problem) => (
                  <button
                    key={problem.id}
                    className={`list-group-item list-group-item-action d-flex justify-content-between align-items-center ${
                      selectedProblem?.id === problem.id ? 'active' : ''
                    }`}
                    onClick={() => setSelectedProblem(problem)}
                  >
                    <div>
                      <strong>{problem.title}</strong>
                      <br />
                      <small className="text-muted">
                        Time: {problem.timeLimitSeconds}s | Memory:{' '}
                        {problem.memoryLimitMb}MB
                      </small>
                    </div>
                    {getDifficultyBadge(problem.difficulty)}
                  </button>
                ))}
                {problems.length === 0 && (
                  <div className="list-group-item text-center text-muted py-4">
                    No problems found
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Pagination */}
          {totalPages > 1 && (
            <nav className="mt-3">
              <ul className="pagination justify-content-center">
                <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                  <button
                    className="page-link"
                    onClick={() => setPage((p) => Math.max(0, p - 1))}
                  >
                    Previous
                  </button>
                </li>
                <li className="page-item disabled">
                  <span className="page-link">
                    {page + 1} / {totalPages}
                  </span>
                </li>
                <li
                  className={`page-item ${
                    page >= totalPages - 1 ? 'disabled' : ''
                  }`}
                >
                  <button
                    className="page-link"
                    onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                  >
                    Next
                  </button>
                </li>
              </ul>
            </nav>
          )}
        </div>

        {/* Problem Details */}
        {selectedProblem && (
          <div className="col-md-7">
            <div className="card">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h5 className="mb-0">{selectedProblem.title}</h5>
                {getDifficultyBadge(selectedProblem.difficulty)}
              </div>
              <div
                className="card-body"
                style={{ maxHeight: '70vh', overflowY: 'auto' }}
              >
                <div
                  dangerouslySetInnerHTML={{
                    __html: selectedProblem.description.replace(/\n/g, '<br/>'),
                  }}
                />

                {selectedProblem.inputFormat && (
                  <div className="mt-4">
                    <h6>Input Format</h6>
                    <p>{selectedProblem.inputFormat}</p>
                  </div>
                )}

                {selectedProblem.outputFormat && (
                  <div className="mt-3">
                    <h6>Output Format</h6>
                    <p>{selectedProblem.outputFormat}</p>
                  </div>
                )}

                {selectedProblem.constraints && (
                  <div className="mt-3">
                    <h6>Constraints</h6>
                    <p>{selectedProblem.constraints}</p>
                  </div>
                )}

                {selectedProblem.sampleTestCases &&
                  selectedProblem.sampleTestCases.length > 0 && (
                    <div className="mt-4">
                      <h6>Sample Test Cases</h6>
                      {selectedProblem.sampleTestCases.map((tc, idx) => (
                        <div key={tc.id} className="bg-light p-3 rounded mb-2">
                          <strong>Example {idx + 1}:</strong>
                          <pre className="mb-1 mt-2">
                            <strong>Input:</strong>
                            {'\n'}
                            {tc.input}
                          </pre>
                          <pre className="mb-0">
                            <strong>Output:</strong>
                            {'\n'}
                            {tc.expectedOutput}
                          </pre>
                        </div>
                      ))}
                    </div>
                  )}
              </div>
              <div className="card-footer">
                <small className="text-muted">
                  Time Limit: {selectedProblem.timeLimitSeconds}s | Memory Limit:{' '}
                  {selectedProblem.memoryLimitMb}MB
                </small>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Problems;