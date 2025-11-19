import { useState } from 'react';
import { Search, Plus, Folder, Clock, Sparkles } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';

interface NoteListProps {
  onCreateNote: () => void;
  onViewMascot: () => void;
}

interface Note {
  id: string;
  title: string;
  folder: string;
  date: string;
}

const folders = [
  { name: '영어+', count: 12 },
  { name: '한국사+', count: 8 },
  { name: '수학+', count: 15 },
  { name: '기타+', count: 5 },
];

const recentNotes: Note[] = [
  { id: '1', title: '지금 기본 개념 정리', folder: '개념노트', date: '2025-11-18 09:20:41' },
  { id: '2', title: '영어 문법 복습', folder: '개념노트', date: '2025-11-17 14:30:22' },
  { id: '3', title: '한국사 근현대사 요약', folder: '개념노트', date: '2025-11-16 18:15:10' },
];

export function NoteList({ onCreateNote, onViewMascot }: NoteListProps) {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <div className="max-w-6xl mx-auto p-6 pb-8">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center gap-4 mb-3">
          <button 
            onClick={onViewMascot}
            className="w-14 h-14 rounded-full bg-gradient-to-br from-[#FFF4CC] to-[#FFE999] flex items-center justify-center shadow-md hover:shadow-lg transition-all duration-300 hover:scale-105"
          >
            <span className="text-2xl">🐥</span>
          </button>
          <div>
            <h1 className="text-[#A855DD] mb-1">똥그리</h1>
            <p className="text-[#D9B5FF] text-sm">오늘도 한 장씩 성장해볼까?</p>
          </div>
        </div>
      </div>

      {/* Search Bar */}
      <div className="relative mb-8">
        <div className="flex gap-3">
          <div className="flex-1 relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-[#C4B5D8] w-5 h-5" />
            <Input
              type="text"
              placeholder="노트 검색 또는 폴더명 검색..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-12 pr-4 py-6 rounded-2xl border-2 border-[#E8DFF5] focus:border-[#A855DD] bg-white placeholder:text-[#C4B5D8] shadow-sm"
            />
          </div>
          <Button
            onClick={onViewMascot}
            className="bg-[#A855DD] hover:bg-[#9333CC] text-white rounded-2xl px-6 shadow-md hover:shadow-lg transition-all duration-300"
          >
            <Sparkles className="w-5 h-5" />
          </Button>
          <Button
            onClick={onCreateNote}
            className="bg-[#FFE999] hover:bg-[#FFD966] text-white rounded-2xl px-6 shadow-md hover:shadow-lg transition-all duration-300"
          >
            <Plus className="w-5 h-5" />
          </Button>
        </div>
      </div>

      {/* Folders */}
      <div className="mb-8">
        <h2 className="text-[#A855DD] mb-4 flex items-center gap-2">
          <Folder className="w-5 h-5 text-[#A855DD]" />
          폴더
        </h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
          {folders.map((folder) => (
            <button
              key={folder.name}
              className="bg-white border-2 border-[#E8DFF5] hover:border-[#FF9900] rounded-2xl p-4 transition-all duration-300 hover:shadow-md hover:scale-105 group"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-[#FFF9E6] flex items-center justify-center group-hover:bg-[#FFF4CC] transition-colors">
                  <Folder className="w-5 h-5 text-[#FFD966]" />
                </div>
                <div className="flex-1 text-left">
                  <div className="text-[#A855DD]">{folder.name}</div>
                  <div className="text-sm text-[#D9B5FF]">{folder.count}개</div>
                </div>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Recent Notes */}
      <div>
        <h2 className="text-[#A855DD] mb-4 flex items-center gap-2">
          <Clock className="w-5 h-5 text-[#A855DD]" />
          최근 노트
        </h2>
        <div className="space-y-3">
          {recentNotes.map((note) => (
            <div
              key={note.id}
              className="bg-white border-2 border-[#E8DFF5] hover:border-[#A855DD] rounded-2xl p-5 transition-all duration-300 hover:shadow-md cursor-pointer group"
            >
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h3 className="text-[#A855DD] mb-1 group-hover:text-[#9333CC] transition-colors">
                    {note.title}
                  </h3>
                  <div className="flex items-center gap-3 text-sm">
                    <span className="px-3 py-1 bg-[#F5F0FB] text-[#A855DD] rounded-full">
                      {note.folder}
                    </span>
                    <span className="text-[#D9B5FF]">{note.date}</span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}